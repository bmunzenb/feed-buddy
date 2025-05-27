pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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
