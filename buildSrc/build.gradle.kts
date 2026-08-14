plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.jvm.plugin)
    implementation(libs.detekt.gradlePlugin)
}
