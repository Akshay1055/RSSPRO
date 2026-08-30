package `in`.swayamsevak.app.data.repository

import `in`.swayamsevak.app.data.model.LoginRequest
import `in`.swayamsevak.app.data.model.LoginResponse
import `in`.swayamsevak.app.data.model.RegistrationRequest
import `in`.swayamsevak.app.data.model.RegistrationResponse
import `in`.swayamsevak.app.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(phoneNumber: String, otp: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(phoneNumber, otp))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegistrationRequest): Result<RegistrationResponse> {
        return try {
            val response = apiService.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
