plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

group = "com.munzenberger"
version = "2.2"

base {
    archivesName = "${rootProject.name}-${project.name}"
}
