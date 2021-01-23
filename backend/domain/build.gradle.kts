import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.google.protobuf") version "0.8.13"
    kotlin("jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    api("io.grpc:grpc-kotlin-stub:1.0.0")
}

description = "domain"

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
    sourceSets.main.get().java.setSrcDirs(listOf(
        "build/generated/source/proto/main/grpckt",
        "build/generated/source/proto/main/java"
    ))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.34.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.0.0:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}
