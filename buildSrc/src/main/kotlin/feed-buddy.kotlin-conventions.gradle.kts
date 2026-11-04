plugins {
    id("org.jetbrains.kotlin.jvm")
    id("dev.detekt")
}

kotlin {
    jvmToolchain(21)
}

detekt {
    baseline = file("$projectDir/detekt-baseline.xml")
}

group = "com.munzenberger"
version = "2.6.0"

base {
    archivesName = "${rootProject.name}-${project.name}"
}
