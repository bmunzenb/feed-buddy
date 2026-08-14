pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("io.github.ben-manes.versions.settings") version "0.61.0"
}

rootProject.name = "feed-buddy"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("core")
include("app")
include("sample-config")
