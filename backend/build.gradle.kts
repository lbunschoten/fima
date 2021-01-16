plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.4.21" apply false
    kotlin("plugin.serialization") version "1.4.21" apply false
}

allprojects {
    repositories {
        maven {
            println(System.getenv())
            credentials {
                username = properties["nexus_username"].toString()
                password = properties["nexus_password"].toString()
            }
            url = uri(properties["nexus_url"].toString())
            isAllowInsecureProtocol = true
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
