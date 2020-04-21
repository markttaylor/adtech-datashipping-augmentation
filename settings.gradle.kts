pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    // resolutionStrategy {
    //     eachPlugin {
    //         if (requested.id.namespace == "io.quarkus.") {
    //             useVersion("1.3.1.Final")
    //         }
    //     }
    // }

    val quarkusPluginVersion: String by settings
    val kotlinVersion: String by settings
    plugins {
        id("io.quarkus") version quarkusPluginVersion
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    }
}

rootProject.name = "adtech-datashipping-augmentation"

