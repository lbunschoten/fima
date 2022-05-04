import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.google.protobuf") version "0.8.18"
    kotlin("jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    api("io.grpc:grpc-stub:1.46.0")
    api("io.grpc:grpc-protobuf:1.46.0")
    api("com.google.protobuf:protobuf-java-util:3.20.1")
    api("com.google.protobuf:protobuf-kotlin:3.20.1")
    api("io.grpc:grpc-kotlin-stub:1.2.1")
}

description = "domain"

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
    sourceSets.main.get().java.setSrcDirs(listOf(
        "build/generated/source/proto/main/grpckt",
        "build/generated/source/proto/main/kotlin",
        "build/generated/source/proto/main/java"
    ))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
    kotlinOptions.freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.20.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.46.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.2.1:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}
