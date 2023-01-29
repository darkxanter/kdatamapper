rootProject.name = "kdatamapper"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    val kspVersion: String by settings
    val dokkaVersion: String by settings
    val gradleNexusPublishVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("io.github.gradle-nexus.publish-plugin") version gradleNexusPublishVersion
        id("com.google.devtools.ksp") version kspVersion
    }
}

includeBuild("build-logic")
include(":core")
include(":processor")
include(":example")
