import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "transaction-import-service"

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.21")
    implementation("io.netty:netty-codec-http2:4.1.55.Final")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("io.grpc:grpc-netty:1.34.1")
    implementation("io.grpc:grpc-protobuf:1.34.1")
    implementation("com.opencsv:opencsv:5.3")
    implementation(project(":domain"))
    runtimeOnly("io.netty:netty-handler-proxy:4.1.55.Final")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.3.2")
    testImplementation("io.mockk:mockk:1.10.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

tasks.register("package") {
    dependsOn("shadowJar")
}