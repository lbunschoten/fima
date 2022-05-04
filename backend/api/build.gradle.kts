import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "api"

plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "2.6.7"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.6.7")
    implementation("org.springframework.boot:spring-boot-starter-test:2.6.7")
    implementation("io.projectreactor:reactor-core:3.4.17")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("io.grpc:grpc-okhttp:1.46.0")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20")
    implementation("io.netty:netty-codec-http2:4.1.76.Final")
    implementation("io.grpc:grpc-netty:1.46.0")
    implementation("io.grpc:grpc-protobuf:1.46.0")
    implementation(project(":domain"))
    runtimeOnly("io.netty:netty-handler-proxy:4.1.76.Final")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.3.0")
    testImplementation("io.mockk:mockk:1.12.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

tasks.register("package") {
    dependsOn("bootJar")
}