package `in`.swayamsevak.backend

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.mindrot.jbcrypt.BCrypt
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.util.*

object Users : Table("users") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val phoneNumber = text("phone_number").uniqueIndex()
    val email = text("email").nullable().uniqueIndex()
    val pinHash = text("pin_hash").nullable()
    val fullName = text("full_name")
    val fatherName = text("father_name").nullable()
    val dob = text("dob").nullable()
    val role = text("role").default("स्वयंसेवक")
    override val primaryKey = PrimaryKey(id)
}

object Organizations : Table("organizations") {
    val id = integer("id").autoIncrement()
    val userId = uuid("user_id").references(Users.id)
    val metro = text("metro").nullable()
    val city = text("city").nullable()
    val basti = text("basti").nullable()
    val branch = text("branch").nullable()
    val mohalla = text("mohalla").nullable()
    override val primaryKey = PrimaryKey(id)
}

object AdminUsers : Table("admin_users") {
    val id = integer("id").autoIncrement()
    val username = text("username").uniqueIndex()
    val pinHash = text("pin_hash")
    override val primaryKey = PrimaryKey(id)
}

object PasswordResetTokens : Table("password_reset_tokens") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val userId = uuid("user_id").references(Users.id)
    val codeHash = text("code_hash")
    val expiresAt = long("expires_at")
    override val primaryKey = PrimaryKey(id)
}

object ShakhaActivities : Table("shakha_activities") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val shakha = text("shakha")
    val type = text("type")
    val title = text("title")
    val details = text("details")
    val createdBy = uuid("created_by").references(Users.id)
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}

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
data class OrganizationDto(val metro: String, val city: String, val basti: String, val branch: String, val mohalla: String)

@Serializable
data class RegistrationResponse(val registrationId: String, val status: String)

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

private fun requireEnv(name: String): String = System.getenv(name) ?: error("$name environment variable must be set")
private fun now(): Long = System.currentTimeMillis()

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port = port, host = "0.0.0.0") {
        Database.connect(
            url = requireEnv("DATABASE_URL"),
            driver = "org.postgresql.Driver",
            user = requireEnv("DATABASE_USER"),
            password = requireEnv("DATABASE_PASSWORD")
        )
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Users, Organizations, AdminUsers, PasswordResetTokens, ShakhaActivities
            )
            seedAdminFromEnvironment()
        }
        module()
    }
    server.start(wait = true)
}

private fun seedAdminFromEnvironment() {
    val username = System.getenv("ADMIN_USERNAME")?.trim()
    val pin = System.getenv("ADMIN_PIN")
    if (username.isNullOrBlank() || pin.isNullOrBlank()) return
    if (AdminUsers.selectAll().where { AdminUsers.username eq username }.empty()) {
        AdminUsers.insert {
            it[AdminUsers.username] = username
            it[pinHash] = BCrypt.hashpw(pin, BCrypt.gensalt())
        }
    }
}

private fun sendResetCode(email: String, code: String): Boolean {
    val apiKey = System.getenv("RESEND_API_KEY") ?: return false
    val from = System.getenv("EMAIL_FROM") ?: return false
    return try {
        val connection = URL("https://api.resend.com/emails").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Bearer " + apiKey)
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        val body = "{\"from\":\"$from\",\"to\":[\"$email\"],\"subject\":\"RSSPRO PIN reset\",\"html\":\"<p>Your PIN reset code is <strong>$code</strong>.</p><p>This code expires in 15 minutes.</p>\"}"
        connection.outputStream.bufferedWriter().use { it.write(body) }
        connection.responseCode in 200..299
    } catch (error: Exception) {
        false
    }
}

fun Application.module() {
    install(ContentNegotiation) { json(Json { prettyPrint = true; isLenient = true; ignoreUnknownKeys = true }) }

    routing {
        get("/") { call.respondText("RSSPRO backend is running") }
        get("/health") { call.respond(mapOf("status" to "ok")) }
        get("/api/ping") { call.respond(mapOf("message" to "RSSPRO backend is running")) }

        get("/members") {
            val query = call.request.queryParameters["query"]?.trim().orEmpty()
            if (query.length < 2) return@get call.respond(emptyList<MemberSummary>())
            val pattern = "%" + query + "%"
            val members = transaction {
                (Users innerJoin Organizations).selectAll().where {
                    (Users.fullName like pattern) or (Organizations.branch like pattern)
                }.limit(50).map {
                    MemberSummary(it[Users.id].toString(), it[Users.fullName], it[Organizations.branch] ?: "")
                }
            }
            call.respond(members)
        }

        get("/shakha/activities") {
            val shakha = call.request.queryParameters["shakha"]?.trim().orEmpty()
            if (shakha.isBlank()) return@get call.respond(emptyList<ActivityResponse>())
            val activities = transaction {
                ShakhaActivities.selectAll().where { ShakhaActivities.shakha eq shakha }
                    .orderBy(ShakhaActivities.createdAt, SortOrder.DESC).limit(50).map {
                        ActivityResponse(
                            it[ShakhaActivities.id].toString(), it[ShakhaActivities.shakha],
                            it[ShakhaActivities.type], it[ShakhaActivities.title], it[ShakhaActivities.details]
                        )
                    }
            }
            call.respond(activities)
        }

        post("/shakha/activities") {
            val request = call.receive<CreateActivityRequest>()
            val creator = transaction {
                (Users innerJoin Organizations).selectAll().where { Users.email eq request.email.trim().lowercase() }.singleOrNull()
            } ?: return@post call.respond(io.ktor.http.HttpStatusCode.Unauthorized, SimpleResponse(false, "Please sign in first"))

            if (request.title.isBlank() || request.details.isBlank()) {
                return@post call.respond(io.ktor.http.HttpStatusCode.BadRequest, SimpleResponse(false, "Title and details are required"))
            }
            val id = UUID.randomUUID()
            transaction {
                ShakhaActivities.insert {
                    it[ShakhaActivities.id] = id
                    it[shakha] = creator[Organizations.branch] ?: ""
                    it[type] = request.type
                    it[title] = request.title.trim()
                    it[details] = request.details.trim()
                    it[createdBy] = creator[Users.id]
                    it[createdAt] = now()
                }
            }
            call.respond(SimpleResponse(true, id.toString()))
        }

        post("/admin/login") {
            val request = call.receive<AdminLoginRequest>()
            val admin = transaction {
                AdminUsers.selectAll().where { AdminUsers.username eq request.username.trim() }.singleOrNull()
            }
            if (admin != null && BCrypt.checkpw(request.pin, admin[AdminUsers.pinHash])) {
                call.respond(AdminLoginResponse(true))
            } else {
                call.respond(io.ktor.http.HttpStatusCode.Unauthorized, AdminLoginResponse(false))
            }
        }

        route("/auth") {
            post("/register") {
                val request = call.receive<RegistrationRequest>()
                if (request.pin.length < 4 || !request.email.contains("@")) {
                    return@post call.respond(io.ktor.http.HttpStatusCode.BadRequest, SimpleResponse(false, "Enter a valid email and PIN"))
                }
                try {
                    val registration = transaction {
                        val userId = Users.insert {
                            it[phoneNumber] = request.phoneNumber
                            it[email] = request.email.trim().lowercase()
                            it[pinHash] = BCrypt.hashpw(request.pin, BCrypt.gensalt())
                            it[fullName] = request.fullName
                            it[fatherName] = request.fatherName
                            it[dob] = request.dob
                        } get Users.id
                        Organizations.insert {
                            it[Organizations.userId] = userId
                            it[metro] = request.organization.metro
                            it[city] = request.organization.city
                            it[basti] = request.organization.basti
                            it[branch] = request.organization.branch
                            it[mohalla] = request.organization.mohalla
                        }
                        "SVY-" + userId.toString().take(6).uppercase()
                    }
                    call.respond(RegistrationResponse(registration, "SUCCESS"))
                } catch (error: Exception) {
                    call.respond(io.ktor.http.HttpStatusCode.Conflict, SimpleResponse(false, "Mobile number or email is already registered"))
                }
            }

            post("/profile-login") {
                val request = call.receive<ProfileLoginRequest>()
                val profile = transaction {
                    (Users innerJoin Organizations).selectAll()
                        .where { Users.email eq request.email.trim().lowercase() }
                        .singleOrNull()
                }
                if (profile != null && !profile[Users.pinHash].isNullOrBlank() && BCrypt.checkpw(request.pin, profile[Users.pinHash]!!)) {
                    call.respond(ProfileResponse(
                        profile[Users.id].toString(), profile[Users.fullName], profile[Users.phoneNumber],
                        profile[Users.email] ?: "", profile[Organizations.branch] ?: "", profile[Users.role]
                    ))
                } else {
                    call.respond(io.ktor.http.HttpStatusCode.Unauthorized, SimpleResponse(false, "Invalid email or PIN"))
                }
            }

            post("/forgot-pin") {
                val request = call.receive<ForgotPinRequest>()
                val user = transaction {
                    Users.selectAll().where { Users.email eq request.email.trim().lowercase() }.singleOrNull()
                }
                if (user == null) {
                    return@post call.respond(SimpleResponse(true, "If this email is registered, a reset code has been sent."))
                }
                val code = String.format("%06d", SecureRandom().nextInt(1_000_000))
                transaction {
                    PasswordResetTokens.deleteWhere { PasswordResetTokens.userId eq user[Users.id] }
                    PasswordResetTokens.insert {
                        it[userId] = user[Users.id]
                        it[codeHash] = BCrypt.hashpw(code, BCrypt.gensalt())
                        it[expiresAt] = now() + 15 * 60 * 1000
                    }
                }
                if (sendResetCode(request.email.trim().lowercase(), code)) {
                    call.respond(SimpleResponse(true, "A reset code has been sent to your email."))
                } else {
                    call.respond(io.ktor.http.HttpStatusCode.ServiceUnavailable, SimpleResponse(false, "Email delivery is not configured yet."))
                }
            }

            post("/reset-pin") {
                val request = call.receive<ResetPinRequest>()
                if (request.newPin.length < 4) {
                    return@post call.respond(io.ktor.http.HttpStatusCode.BadRequest, SimpleResponse(false, "PIN must have at least 4 characters"))
                }
                val user = transaction {
                    Users.selectAll().where { Users.email eq request.email.trim().lowercase() }.singleOrNull()
                } ?: return@post call.respond(io.ktor.http.HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid reset request"))
                val token = transaction {
                    PasswordResetTokens.selectAll().where { PasswordResetTokens.userId eq user[Users.id] }
                        .orderBy(PasswordResetTokens.expiresAt, SortOrder.DESC).limit(1).singleOrNull()
                }
                if (token == null || token[PasswordResetTokens.expiresAt] < now() || !BCrypt.checkpw(request.code, token[PasswordResetTokens.codeHash])) {
                    return@post call.respond(io.ktor.http.HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid or expired reset code"))
                }
                transaction {
                    Users.update({ Users.id eq user[Users.id] }) { it[pinHash] = BCrypt.hashpw(request.newPin, BCrypt.gensalt()) }
                    PasswordResetTokens.deleteWhere { PasswordResetTokens.userId eq user[Users.id] }
                }
                call.respond(SimpleResponse(true, "PIN updated successfully"))
            }
        }
    }
}
