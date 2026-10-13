package `in`.swayamsevak.app.data.remote

import `in`.swayamsevak.app.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegistrationRequest): RegistrationResponse

    @POST("auth/profile-login")
    suspend fun profileLogin(@Body request: ProfileLoginRequest): ProfileResponse

    @POST("auth/forgot-pin")
    suspend fun forgotPin(@Body request: ForgotPinRequest): SimpleResponse

    @POST("auth/reset-pin")
    suspend fun resetPin(@Body request: ResetPinRequest): SimpleResponse

    @GET("members")
    suspend fun searchMembers(@Query("query") query: String): List<MemberSummary>

    @POST("shakha/activities")
    suspend fun createActivity(@Body request: CreateActivityRequest): SimpleResponse

    @GET("shakha/activities")
    suspend fun activities(@Query("shakha") shakha: String): List<ActivityResponse>

    @POST("admin/login")
    suspend fun adminLogin(@Body request: AdminLoginRequest): AdminLoginResponse
}
