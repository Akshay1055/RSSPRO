package `in`.swayamsevak.app.ui.auth

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.swayamsevak.app.data.model.MemberSummary
import `in`.swayamsevak.app.data.model.OrganizationDto
import `in`.swayamsevak.app.data.model.RegistrationRequest
import `in`.swayamsevak.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _memberResults = MutableStateFlow<List<MemberSummary>>(emptyList())
    val memberResults = _memberResults.asStateFlow()

    private val _directoryState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val directoryState = _directoryState.asStateFlow()

    private val _adminState = MutableStateFlow<AdminState>(AdminState.Idle)
    val adminState = _adminState.asStateFlow()

    var phone = MutableStateFlow("")
    var name = MutableStateFlow("")
    var fatherName = MutableStateFlow("")
    var dob = MutableStateFlow("")

    var metro = MutableStateFlow("इंदौर")
    var city = MutableStateFlow("पश्चिम नगर")
    var basti = MutableStateFlow("विजय नगर बस्ती")
    var branch = MutableStateFlow("विजय नगर शाखा")
    var mohalla = MutableStateFlow("विजय नगर")

    val interests = mutableStateListOf<String>()

    fun submitRegistration() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val org = OrganizationDto(metro.value, city.value, basti.value, branch.value, mohalla.value)
            val request = RegistrationRequest(phone.value, name.value, fatherName.value, dob.value, org, interests.toList())

            repository.register(request).fold(
                onSuccess = { _uiState.value = AuthUiState.Registered(it.registrationId) },
                onFailure = { _uiState.value = AuthUiState.Error("यह मोबाइल नंबर पहले से पंजीकृत है या पंजीयन पूरा नहीं हुआ।") }
            )
        }
    }

    fun searchMembers(query: String) {
        if (query.trim().length < 2) {
            _memberResults.value = emptyList()
            _directoryState.value = DirectoryState.Error("कम से कम 2 अक्षर लिखें।")
            return
        }

        viewModelScope.launch {
            _directoryState.value = DirectoryState.Loading
            repository.searchMembers(query.trim()).fold(
                onSuccess = {
                    _memberResults.value = it
                    _directoryState.value = DirectoryState.Ready
                },
                onFailure = {
                    _memberResults.value = emptyList()
                    _directoryState.value = DirectoryState.Error("सदस्य खोज अभी उपलब्ध नहीं है।")
                }
            )
        }
    }

    fun adminLogin(username: String, pin: String) {
        viewModelScope.launch {
            _adminState.value = AdminState.Loading
            repository.adminLogin(username.trim(), pin).fold(
                onSuccess = {
                    _adminState.value = if (it.success) AdminState.Authenticated else AdminState.Error("गलत उपयोगकर्ता नाम या PIN")
                },
                onFailure = { _adminState.value = AdminState.Error("Admin सत्यापन उपलब्ध नहीं है।") }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun resetAdminState() {
        _adminState.value = AdminState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Registered(val regId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

sealed class DirectoryState {
    object Idle : DirectoryState()
    object Loading : DirectoryState()
    object Ready : DirectoryState()
    data class Error(val message: String) : DirectoryState()
}

sealed class AdminState {
    object Idle : AdminState()
    object Loading : AdminState()
    object Authenticated : AdminState()
    data class Error(val message: String) : AdminState()
}
