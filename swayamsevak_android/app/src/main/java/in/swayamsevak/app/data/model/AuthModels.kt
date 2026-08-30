package `in`.swayamsevak.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phoneNumber: String,
    val otp: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class RegistrationRequest(
    val phoneNumber: String,
    val fullName: String,
    val fatherName: String,
    val dob: String,
    val organization: OrganizationDto,
    val interests: List<String>
)

@Serializable
data class RegistrationResponse(
    val registrationId: String,
    val status: String
)

@Serializable
data class UserDto(
    val id: String,
    val fullName: String,
    val role: String
)

@Serializable
data class OrganizationDto(
    val metro: String,
    val city: String,
    val basti: String,
    val branch: String,
    val mohalla: String
)
