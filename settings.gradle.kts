pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
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
