import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "transaction-service"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.0.1")
    implementation("io.netty:netty-codec-http2:4.1.55.Final")
    implementation("io.grpc:grpc-netty:1.34.1")
    implementation("io.grpc:grpc-protobuf:1.34.1")
    implementation("org.apache.kafka:kafka-clients:2.6.0")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("org.jdbi:jdbi3-core:3.18.0")
    implementation("org.jdbi:jdbi3-kotlin:3.18.0")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.18.0")
    implementation("mysql:mysql-connector-java:8.0.22")
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

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-Xshare:off")
}