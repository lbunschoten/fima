plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.4.21" apply false
    kotlin("plugin.serialization") version "1.4.21" apply false
    id("com.github.johnrengelman.shadow") version "6.1.0" apply false
}

allprojects {
    repositories {
        maven {
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }
            url = uri(System.getenv("NEXUS_URL"))
            isAllowInsecureProtocol = true
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = true
        isReproducibleFileOrder = true
    }
}
