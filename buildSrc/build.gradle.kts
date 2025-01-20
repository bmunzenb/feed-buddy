plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.+")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.+")
}

dependencyLocking {
    lockAllConfigurations()
}
