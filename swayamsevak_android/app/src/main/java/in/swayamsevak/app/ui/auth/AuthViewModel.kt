package `in`.swayamsevak.app.ui.auth

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.swayamsevak.app.data.model.*
import `in`.swayamsevak.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()
    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile = _profile.asStateFlow()
    private val _accountState = MutableStateFlow<AccountState>(AccountState.Idle)
    val accountState = _accountState.asStateFlow()
    private val _memberResults = MutableStateFlow<List<MemberSummary>>(emptyList())
    val memberResults = _memberResults.asStateFlow()
    private val _directoryState = MutableStateFlow<DirectoryState>(DirectoryState.Idle)
    val directoryState = _directoryState.asStateFlow()
    private val _adminState = MutableStateFlow<AdminState>(AdminState.Idle)
    val adminState = _adminState.asStateFlow()
    private val _activityState = MutableStateFlow<ActivityState>(ActivityState.Idle)
    val activityState = _activityState.asStateFlow()

    var phone = MutableStateFlow("")
    var email = MutableStateFlow("")
    var pin = MutableStateFlow("")
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
            val request = RegistrationRequest(
                phone.value, email.value.trim(), pin.value, name.value, fatherName.value, dob.value,
                OrganizationDto(metro.value, city.value, basti.value, branch.value, mohalla.value),
                interests.toList()
            )
            repository.register(request).fold(
                onSuccess = {
                    _profile.value = ProfileResponse(it.registrationId, name.value, phone.value, email.value, branch.value, "स्वयंसेवक")
                    _uiState.value = AuthUiState.Registered(it.registrationId)
                },
                onFailure = { _uiState.value = AuthUiState.Error("मोबाइल नंबर या ईमेल पहले से पंजीकृत है, अथवा विवरण अधूरा है।") }
            )
        }
    }

    fun profileLogin(loginEmail: String, loginPin: String) {
        viewModelScope.launch {
            _accountState.value = AccountState.Loading
            repository.profileLogin(loginEmail, loginPin).fold(
                onSuccess = { _profile.value = it; _accountState.value = AccountState.Authenticated },
                onFailure = { _accountState.value = AccountState.Error("ईमेल या PIN सही नहीं है।") }
            )
        }
    }

    fun sendForgotPin(emailAddress: String) {
        viewModelScope.launch {
            _accountState.value = AccountState.Loading
            repository.forgotPin(emailAddress).fold(
                onSuccess = { _accountState.value = if (it.success) AccountState.Message(it.message) else AccountState.Error(it.message) },
                onFailure = { _accountState.value = AccountState.Error("रीसेट ईमेल नहीं भेजा जा सका।") }
            )
        }
    }

    fun resetPin(emailAddress: String, code: String, newPin: String) {
        viewModelScope.launch {
            _accountState.value = AccountState.Loading
            repository.resetPin(emailAddress, code, newPin).fold(
                onSuccess = { _accountState.value = if (it.success) AccountState.Message("PIN बदल दिया गया है। अब प्रवेश करें।") else AccountState.Error(it.message) },
                onFailure = { _accountState.value = AccountState.Error("रीसेट कोड अमान्य या समाप्त हो गया है।") }
            )
        }
    }

    fun createActivity(type: String, title: String, details: String) {
        val profileValue = _profile.value ?: run {
            _activityState.value = ActivityState.Error("गतिविधि बनाने के लिए पहले प्रोफ़ाइल में प्रवेश करें।")
            return
        }
        viewModelScope.launch {
            _activityState.value = ActivityState.Loading
            repository.createActivity(profileValue.email, type, title, details).fold(
                onSuccess = { _activityState.value = if (it.success) ActivityState.Message("गतिविधि शाखा में प्रकाशित हो गई है।") else ActivityState.Error(it.message) },
                onFailure = { _activityState.value = ActivityState.Error("गतिविधि प्रकाशित नहीं हो सकी।") }
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
                onSuccess = { _memberResults.value = it; _directoryState.value = DirectoryState.Ready },
                onFailure = { _memberResults.value = emptyList(); _directoryState.value = DirectoryState.Error("सदस्य खोज अभी उपलब्ध नहीं है।") }
            )
        }
    }

    fun adminLogin(username: String, adminPin: String) {
        viewModelScope.launch {
            _adminState.value = AdminState.Loading
            repository.adminLogin(username.trim(), adminPin).fold(
                onSuccess = { _adminState.value = if (it.success) AdminState.Authenticated else AdminState.Error("गलत उपयोगकर्ता नाम या PIN") },
                onFailure = { _adminState.value = AdminState.Error("Admin सत्यापन उपलब्ध नहीं है।") }
            )
        }
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }
    fun resetAdminState() { _adminState.value = AdminState.Idle }
}

sealed class AuthUiState { object Idle : AuthUiState(); object Loading : AuthUiState(); data class Registered(val regId: String) : AuthUiState(); data class Error(val message: String) : AuthUiState() }
sealed class AccountState { object Idle : AccountState(); object Loading : AccountState(); object Authenticated : AccountState(); data class Message(val message: String) : AccountState(); data class Error(val message: String) : AccountState() }
sealed class DirectoryState { object Idle : DirectoryState(); object Loading : DirectoryState(); object Ready : DirectoryState(); data class Error(val message: String) : DirectoryState() }
sealed class ActivityState { object Idle : ActivityState(); object Loading : ActivityState(); data class Message(val message: String) : ActivityState(); data class Error(val message: String) : ActivityState() }
sealed class AdminState { object Idle : AdminState(); object Loading : AdminState(); object Authenticated : AdminState(); data class Error(val message: String) : AdminState() }
