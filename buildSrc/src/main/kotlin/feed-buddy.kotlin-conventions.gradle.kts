plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

group = "com.munzenberger"
version = "2.4.3"

base {
    archivesName = "${rootProject.name}-${project.name}"
}
