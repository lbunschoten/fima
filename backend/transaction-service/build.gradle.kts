import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "transaction-service"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.netty:netty-codec-http2:4.1.107.Final")
    implementation("io.grpc:grpc-netty:1.62.2")
    implementation("io.grpc:grpc-util:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("com.opencsv:opencsv:5.9")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("org.jdbi:jdbi3-core:3.45.1")
    implementation("org.jdbi:jdbi3-kotlin:3.45.1")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.45.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("io.ktor:ktor-server-core-jvm:2.3.9")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.9")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.9")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.9")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.3.9")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
    implementation("io.ktor:ktor-server-cors:2.3.9")
    implementation(project(":domain"))
    runtimeOnly("io.netty:netty-handler-proxy:4.1.107.Final")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.1")
    testImplementation("io.mockk:mockk:1.13.10")
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-Xshare:off")
}

tasks.register("package") {
    dependsOn("shadowJar")
}