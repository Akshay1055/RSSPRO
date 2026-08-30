package `in`.swayamsevak.app.ui.auth

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Registration Temp State
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

    fun login() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(phone.value, "1234").fold( // Mocking OTP for demo
                onSuccess = { _uiState.value = AuthUiState.Success(it.token) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Connection failed. Is server running?") }
            )
        }
    }

    fun submitRegistration() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val org = OrganizationDto(metro.value, city.value, basti.value, branch.value, mohalla.value)
            val request = RegistrationRequest(phone.value, name.value, fatherName.value, dob.value, org, interests.toList())
            
            repository.register(request).fold(
                onSuccess = { _uiState.value = AuthUiState.Registered(it.registrationId) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val token: String) : AuthUiState()
    data class Registered(val regId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
