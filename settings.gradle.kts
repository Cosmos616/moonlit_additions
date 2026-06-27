pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
    }
}

rootProject.name = "Moonlit"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
