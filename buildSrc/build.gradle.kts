plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.jvm.plugin)
    implementation(libs.detekt.plugin)
    implementation(libs.ktlint.plugin)
}
