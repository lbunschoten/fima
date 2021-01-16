import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "api"

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("org.springframework.boot") version "2.4.1"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.4.1")
    implementation("org.springframework.boot:spring-boot-starter-test:2.4.1")
    implementation("io.projectreactor:reactor-core:3.4.1")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")
    implementation("io.grpc:grpc-okhttp:1.34.1")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
    implementation("io.netty:netty-codec-http2:4.1.55.Final")
    implementation("io.grpc:grpc-netty:1.34.1")
    implementation("io.grpc:grpc-protobuf:1.34.1")
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

//tasks.withType<ShadowJar> {
//    minimize()
//}

