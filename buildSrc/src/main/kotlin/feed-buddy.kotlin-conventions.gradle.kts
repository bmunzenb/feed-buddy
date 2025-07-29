plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    compilerOptions {
        // https://youtrack.jetbrains.com/issue/KT-73255
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

ktlint {
    // https://github.com/pinterest/ktlint/releases
    version.set("1.7.1")
}

group = "com.munzenberger"
version = "2.4.1"

base {
    archivesName = "${rootProject.name}-${project.name}"
}
