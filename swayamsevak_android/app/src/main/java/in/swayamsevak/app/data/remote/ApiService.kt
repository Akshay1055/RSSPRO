package `in`.swayamsevak.app.data.remote

import `in`.swayamsevak.app.data.model.LoginRequest
import `in`.swayamsevak.app.data.model.LoginResponse
import `in`.swayamsevak.app.data.model.RegistrationRequest
import `in`.swayamsevak.app.data.model.RegistrationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegistrationRequest): RegistrationResponse
}
