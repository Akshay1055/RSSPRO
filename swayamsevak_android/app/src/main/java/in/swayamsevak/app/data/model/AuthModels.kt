package `in`.swayamsevak.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val phoneNumber: String,
    val email: String,
    val pin: String,
    val fullName: String,
    val fatherName: String,
    val dob: String,
    val organization: OrganizationDto,
    val interests: List<String>
)

@Serializable
data class RegistrationResponse(val registrationId: String, val status: String)

@Serializable
data class OrganizationDto(val metro: String, val city: String, val basti: String, val branch: String, val mohalla: String)

@Serializable
data class MemberSummary(val id: String, val fullName: String, val shakha: String)

@Serializable
data class ProfileLoginRequest(val email: String, val pin: String)

@Serializable
data class ProfileResponse(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val shakha: String,
    val role: String
)

@Serializable
data class ForgotPinRequest(val email: String)

@Serializable
data class ResetPinRequest(val email: String, val code: String, val newPin: String)

@Serializable
data class SimpleResponse(val success: Boolean, val message: String)

@Serializable
data class AdminLoginRequest(val username: String, val pin: String)

@Serializable
data class AdminLoginResponse(val success: Boolean)

@Serializable
data class CreateActivityRequest(val email: String, val type: String, val title: String, val details: String)

@Serializable
data class ActivityResponse(val id: String, val shakha: String, val type: String, val title: String, val details: String)
