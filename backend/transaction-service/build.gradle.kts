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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("io.netty:netty-codec-http2:4.1.76.Final")
    implementation("io.grpc:grpc-netty:1.46.0")
    implementation("io.grpc:grpc-protobuf:1.46.0")
    implementation("com.opencsv:opencsv:5.6")
    implementation("org.apache.kafka:kafka-clients:3.1.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("org.jdbi:jdbi3-core:3.28.0")
    implementation("org.jdbi:jdbi3-kotlin:3.28.0")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.28.0")
    implementation("org.postgresql:postgresql:42.3.4")
    implementation(project(":domain"))
    runtimeOnly("io.netty:netty-handler-proxy:4.1.76.Final")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.6.3")
    testImplementation("io.mockk:mockk:1.12.3")
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