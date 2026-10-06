package `in`.swayamsevak.app.data.repository

import `in`.swayamsevak.app.data.model.AdminLoginRequest
import `in`.swayamsevak.app.data.model.AdminLoginResponse
import `in`.swayamsevak.app.data.model.LoginRequest
import `in`.swayamsevak.app.data.model.LoginResponse
import `in`.swayamsevak.app.data.model.MemberSummary
import `in`.swayamsevak.app.data.model.RegistrationRequest
import `in`.swayamsevak.app.data.model.RegistrationResponse
import `in`.swayamsevak.app.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(phoneNumber: String, otp: String): Result<LoginResponse> = runCatching {
        apiService.login(LoginRequest(phoneNumber, otp))
    }

    suspend fun register(request: RegistrationRequest): Result<RegistrationResponse> = runCatching {
        apiService.register(request)
    }

    suspend fun searchMembers(query: String): Result<List<MemberSummary>> = runCatching {
        apiService.searchMembers(query)
    }

    suspend fun adminLogin(username: String, pin: String): Result<AdminLoginResponse> = runCatching {
        apiService.adminLogin(AdminLoginRequest(username, pin))
    }
}
