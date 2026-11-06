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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.mindrot.jbcrypt.BCrypt
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.util.*

// Table 1: Mahanagars (महानगर - Name, Karyawah)
object Mahanagars : Table("mahanagars") {
    val id = integer("id").autoIncrement()
    val name = text("name").uniqueIndex()
    val karyawah = text("karyawah").default("राकेश जोशी")
    override val primaryKey = PrimaryKey(id)
}

// Table 2: Nagars (नगर - Mahanagar_Id, Name, Karyawah)
object Nagars : Table("nagars") {
    val id = integer("id").autoIncrement()
    val mahanagarId = integer("mahanagar_id").references(Mahanagars.id)
    val name = text("name")
    val karyawah = text("karyawah").default("राकेश जोशी")
    override val primaryKey = PrimaryKey(id)
}

// Table 3: Shakhas (बस्ती/शाखा/मोहल्ला - Nagar_Id, Basti_Name, Shakha_Name, Mohalla_Name, Mukhya_Shikshak, Karyawah)
object Shakhas : Table("shakhas") {
    val id = integer("id").autoIncrement()
    val nagarId = integer("nagar_id").references(Nagars.id)
    val bastiName = text("basti_name")
    val shakhaName = text("shakha_name")
    val mohallaName = text("mohalla_name").default("")
    val mukhyaShikshak = text("mukhya_shikshak").default("राजेश वर्मा")
    val karyawah = text("karyawah").default("राकेश जोशी")
    override val primaryKey = PrimaryKey(id)
}

// Table 4: Users (Registration Details + Shakha ForeignKey, Mobile-only Auth without PIN/Password)
object Users : Table("users") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val phoneNumber = text("phone_number").uniqueIndex()
    val email = text("email").nullable().uniqueIndex()
    val fullName = text("full_name")
    val fatherName = text("father_name").nullable()
    val dob = text("dob").nullable()
    val occupation = text("occupation").default("व्यवसाय / छात्र")
    val role = text("role").default("स्वयंसेवक")
    val shakhaId = integer("shakha_id").references(Shakhas.id).nullable()
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
    override val primaryKey = PrimaryKey(id)
}

// Table 5: Admin Users (Username & Hashed PIN for Admin Panel access)
object AdminUsers : Table("admin_users") {
    val id = integer("id").autoIncrement()
    val username = text("username").uniqueIndex()
    val pinHash = text("pin_hash")
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
    val pin: String = "0000",
    val fullName: String,
    val fatherName: String,
    val dob: String,
    val occupation: String = "व्यवसाय / छात्र",
    val organization: OrganizationDto,
    val interests: List<String> = emptyList()
)

@Serializable
data class OrganizationDto(
    val metro: String,
    val city: String,
    val basti: String,
    val branch: String,
    val mohalla: String,
    val mukhyaShikshak: String = "राजेश वर्मा",
    val karyawah: String = "राकेश जोशी"
)

@Serializable
data class RegistrationResponse(val registrationId: String, val status: String)

@Serializable
data class MemberSummary(val id: String, val fullName: String, val shakha: String)

@Serializable
data class ProfileLoginRequest(val email: String, val pin: String = "")

@Serializable
data class ProfileResponse(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val shakha: String,
    val role: String,
    val occupation: String = "व्यवसाय / छात्र",
    val mukhyaShikshak: String = "राजेश वर्मा",
    val karyawah: String = "राकेश जोशी"
)

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

@Serializable
data class UpdateUserRequest(
    val fullName: String,
    val fatherName: String,
    val dob: String,
    val occupation: String = "व्यवसाय / छात्र",
    val organization: OrganizationDto
)

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
                Mahanagars, Nagars, Shakhas, Users, AdminUsers, ShakhaActivities
            )
            seedAdminFromEnvironment()
        }
        module()
    }
    server.start(wait = true)
}

private fun seedAdminFromEnvironment() {
    val rawUsernames = System.getenv("ADMIN_USERNAME")?.trim()
    val pin = System.getenv("ADMIN_PIN")
    if (rawUsernames.isNullOrBlank() || pin.isNullOrBlank()) return

    val usernames = rawUsernames.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    for (user in usernames) {
        if (AdminUsers.selectAll().where { AdminUsers.username eq user }.count() == 0L) {
            AdminUsers.insert {
                it[AdminUsers.username] = user
                it[pinHash] = BCrypt.hashpw(pin, BCrypt.gensalt())
            }
        }
    }
}

fun Application.module() {
    install(ContentNegotiation) { json(Json { prettyPrint = true; isLenient = true; ignoreUnknownKeys = true }) }

    routing {
        get("/") { call.respondText("RSSPRO backend is running") }
        get("/health") { call.respond(mapOf("status" to "ok")) }
        get("/api/ping") { call.respond(mapOf("message" to "RSSPRO backend is running")) }

        get("/api/users") {
            try {
                val usersList = transaction {
                    (Users leftJoin Shakhas leftJoin Nagars leftJoin Mahanagars).selectAll().map {
                        mapOf(
                            "id" to it[Users.id].toString(),
                            "phoneNumber" to it[Users.phoneNumber],
                            "fullName" to it[Users.fullName],
                            "fatherName" to (it[Users.fatherName] ?: ""),
                            "dob" to (it[Users.dob] ?: ""),
                            "occupation" to (it[Users.occupation] ?: "व्यवसाय / छात्र"),
                            "role" to it[Users.role],
                            "organization" to mapOf(
                                "metro" to (it.getOrNull(Mahanagars.name) ?: "इंदौर"),
                                "city" to (it.getOrNull(Nagars.name) ?: "पश्चिम नगर"),
                                "basti" to (it.getOrNull(Shakhas.bastiName) ?: "विजय नगर बस्ती"),
                                "branch" to (it.getOrNull(Shakhas.shakhaName) ?: "विजय नगर शाखा"),
                                "mohalla" to (it.getOrNull(Shakhas.mohallaName) ?: "विजय नगर"),
                                "mukhyaShikshak" to (it.getOrNull(Shakhas.mukhyaShikshak) ?: "राजेश वर्मा"),
                                "karyawah" to (it.getOrNull(Shakhas.karyawah) ?: "राकेश जोशी")
                            )
                        )
                    }
                }
                call.respond(usersList)
            } catch (e: Exception) {
                call.respond(io.ktor.http.HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Failed to fetch users")))
            }
        }

        put("/api/users/{id}") {
            val idStr = call.parameters["id"] ?: return@put call.respond(io.ktor.http.HttpStatusCode.BadRequest, "Missing user id")
            val userId = try { UUID.fromString(idStr) } catch (e: Exception) { return@put call.respond(io.ktor.http.HttpStatusCode.BadRequest, "Invalid user id") }
            val req = call.receive<UpdateUserRequest>()

            try {
                transaction {
                    Users.update({ Users.id eq userId }) {
                        it[fullName] = req.fullName
                        it[fatherName] = req.fatherName
                        it[dob] = req.dob
                        it[occupation] = req.occupation
                    }
                }
                call.respond(mapOf("status" to "SUCCESS"))
            } catch (e: Exception) {
                call.respond(io.ktor.http.HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Update failed")))
            }
        }

        get("/members") {
            val query = call.request.queryParameters["query"]?.trim().orEmpty()
            if (query.length < 2) return@get call.respond(emptyList<MemberSummary>())
            val pattern = "%" + query + "%"
            val members = transaction {
                (Users leftJoin Shakhas).selectAll().where {
                    (Users.fullName like pattern) or (Shakhas.shakhaName like pattern)
                }.limit(50).map {
                    MemberSummary(it[Users.id].toString(), it[Users.fullName], it.getOrNull(Shakhas.shakhaName) ?: "विजय नगर शाखा")
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
                Users.selectAll().where { Users.email eq request.email.trim().lowercase() }.singleOrNull()
            } ?: return@post call.respond(io.ktor.http.HttpStatusCode.Unauthorized, SimpleResponse(false, "Please sign in first"))

            if (request.title.isBlank() || request.details.isBlank()) {
                return@post call.respond(io.ktor.http.HttpStatusCode.BadRequest, SimpleResponse(false, "Title and details are required"))
            }
            val id = UUID.randomUUID()
            transaction {
                ShakhaActivities.insert {
                    it[ShakhaActivities.id] = id
                    it[ShakhaActivities.shakha] = "विजय नगर शाखा"
                    it[ShakhaActivities.type] = request.type
                    it[ShakhaActivities.title] = request.title.trim()
                    it[ShakhaActivities.details] = request.details.trim()
                    it[ShakhaActivities.createdBy] = creator[Users.id]
                    it[ShakhaActivities.createdAt] = now()
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
                try {
                    val registration = transaction {
                        val userId = Users.insert {
                            it[phoneNumber] = request.phoneNumber
                            it[email] = request.email.trim().lowercase()
                            it[fullName] = request.fullName
                            it[fatherName] = request.fatherName
                            it[dob] = request.dob
                            it[occupation] = request.occupation
                        } get Users.id
                        "SVY-" + userId.toString().take(6).uppercase()
                    }
                    call.respond(RegistrationResponse(registration, "SUCCESS"))
                } catch (error: Exception) {
                    call.respond(io.ktor.http.HttpStatusCode.Conflict, SimpleResponse(false, "Mobile number or email is already registered"))
                }
            }

            post("/profile-login") {
                val request = call.receive<ProfileLoginRequest>()
                val cleanInput = request.email.trim().lowercase()
                val profile = transaction {
                    (Users leftJoin Shakhas).selectAll()
                        .where { (Users.email eq cleanInput) or (Users.phoneNumber eq cleanInput) }
                        .singleOrNull()
                }
                if (profile != null) {
                    call.respond(ProfileResponse(
                        profile[Users.id].toString(), profile[Users.fullName], profile[Users.phoneNumber],
                        profile[Users.email] ?: "", profile.getOrNull(Shakhas.shakhaName) ?: "विजय नगर शाखा", profile[Users.role],
                        profile[Users.occupation] ?: "व्यवसाय / छात्र"
                    ))
                } else {
                    call.respond(io.ktor.http.HttpStatusCode.Unauthorized, SimpleResponse(false, "Invalid phone or email"))
                }
            }
        }
    }
}
