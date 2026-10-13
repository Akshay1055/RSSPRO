package `in`.swayamsevak.app.data.repository

import `in`.swayamsevak.app.data.model.*
import `in`.swayamsevak.app.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun register(request: RegistrationRequest): Result<RegistrationResponse> = runCatching { apiService.register(request) }
    suspend fun profileLogin(email: String, pin: String): Result<ProfileResponse> = runCatching { apiService.profileLogin(ProfileLoginRequest(email, pin)) }
    suspend fun forgotPin(email: String): Result<SimpleResponse> = runCatching { apiService.forgotPin(ForgotPinRequest(email)) }
    suspend fun resetPin(email: String, code: String, newPin: String): Result<SimpleResponse> = runCatching { apiService.resetPin(ResetPinRequest(email, code, newPin)) }
    suspend fun searchMembers(query: String): Result<List<MemberSummary>> = runCatching { apiService.searchMembers(query) }
    suspend fun createActivity(email: String, type: String, title: String, details: String): Result<SimpleResponse> = runCatching { apiService.createActivity(CreateActivityRequest(email, type, title, details)) }
    suspend fun activities(shakha: String): Result<List<ActivityResponse>> = runCatching { apiService.activities(shakha) }
    suspend fun adminLogin(username: String, pin: String): Result<AdminLoginResponse> = runCatching { apiService.adminLogin(AdminLoginRequest(username, pin)) }
}
