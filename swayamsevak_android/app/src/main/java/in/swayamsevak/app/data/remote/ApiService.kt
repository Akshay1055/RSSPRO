package `in`.swayamsevak.app.data.remote

import `in`.swayamsevak.app.data.model.AdminLoginRequest
import `in`.swayamsevak.app.data.model.AdminLoginResponse
import `in`.swayamsevak.app.data.model.LoginRequest
import `in`.swayamsevak.app.data.model.LoginResponse
import `in`.swayamsevak.app.data.model.MemberSummary
import `in`.swayamsevak.app.data.model.RegistrationRequest
import `in`.swayamsevak.app.data.model.RegistrationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegistrationRequest): RegistrationResponse

    @GET("members")
    suspend fun searchMembers(@Query("query") query: String): List<MemberSummary>

    @POST("admin/login")
    suspend fun adminLogin(@Body request: AdminLoginRequest): AdminLoginResponse
}
