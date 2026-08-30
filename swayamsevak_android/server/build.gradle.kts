plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "in.swayamsevak.backend"
version = "0.0.1"

application {
    mainClass.set("in.swayamsevak.backend.ApplicationKt")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:3.0.1")
    implementation("io.ktor:ktor-server-netty-jvm:3.0.1")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.1")
    implementation("io.ktor:ktor-server-auth-jvm:3.0.1")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:3.0.1")
    implementation("io.ktor:ktor-server-cors-jvm:3.0.1")
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:6.0.0")

    implementation("ch.qos.logback:logback-classic:1.5.11")
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.0.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.0.21")
}
