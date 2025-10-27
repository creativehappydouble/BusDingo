plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-jackson:2.3.12")
}
application { mainClass.set("com.example.server.MainKt") }
