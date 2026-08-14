plugins {
    id("org.jetbrains.kotlin.jvm")
    id("dev.detekt")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

detekt {
    baseline = file("$projectDir/detekt-baseline.xml")
}

group = "com.munzenberger"
version = "2.5.0"

base {
    archivesName = "${rootProject.name}-${project.name}"
}
