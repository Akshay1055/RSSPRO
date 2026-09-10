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
import java.util.*

// Database Tables
object Users : Table("users") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val phoneNumber = text("phone_number").uniqueIndex()
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

// Request/Response DTOs
@Serializable
data class LoginRequest(val phoneNumber: String, val otp: String)

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
data class OrganizationDto(
    val metro: String,
    val city: String,
    val basti: String,
    val branch: String,
    val mohalla: String
)

private fun requireEnv(name: String): String =
    System.getenv(name) ?: error("$name environment variable must be set")

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port = port, host = "0.0.0.0") {
        println("Starting Swayamsevak Backend on 0.0.0.0:$port...")
        
        try {
            Database.connect(
                url = requireEnv("DATABASE_URL"),
                driver = "org.postgresql.Driver",
                user = requireEnv("DATABASE_USER"),
                password = requireEnv("DATABASE_PASSWORD")
            )
            println("Connected to Neon Database!")
            transaction {
                SchemaUtils.create(Users, Organizations)
            }
            println("Database schema initialized!")
        } catch (e: Exception) {
            println("DATABASE ERROR: ${e.message}")
        }

        module()
    }
    server.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    routing {
        get("/") {
            call.respondText("Swayamsevak Backend is running on Cloud DB!")
        }
        
        route("/auth") {
            post("/login") {
                val req = call.receive<LoginRequest>()
                val user = transaction {
                    Users.selectAll().where { Users.phoneNumber eq req.phoneNumber }
                        .map {
                            mapOf(
                                "id" to it[Users.id].toString(),
                                "fullName" to it[Users.fullName],
                                "role" to it[Users.role]
                            )
                        }.singleOrNull()
                }

                if (user != null) {
                    call.respond(mapOf("token" to "jwt-token-${user["id"]}", "user" to user))
                } else {
                    call.respond(io.ktor.http.HttpStatusCode.NotFound, "User not found")
                }
            }

            post("/register") {
                val req = call.receive<RegistrationRequest>()
                try {
                    val regId = transaction {
                        val userId = Users.insert {
                            it[phoneNumber] = req.phoneNumber
                            it[fullName] = req.fullName
                            it[fatherName] = req.fatherName
                            it[dob] = req.dob
                        } get Users.id

                        Organizations.insert {
                            it[Organizations.userId] = userId
                            it[metro] = req.organization.metro
                            it[city] = req.organization.city
                            it[basti] = req.organization.basti
                            it[branch] = req.organization.branch
                            it[mohalla] = req.organization.mohalla
                        }
                        
                        "SVY-2026-${userId.toString().take(6).uppercase()}"
                    }
                    call.respond(mapOf("registrationId" to regId, "status" to "SUCCESS"))
                } catch (e: Exception) {
                    call.respond(io.ktor.http.HttpStatusCode.Conflict, "Phone number already registered")
                }
            }
        }
    }
}
