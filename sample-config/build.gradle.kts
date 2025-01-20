plugins {
    id("feed-buddy.kotlin-conventions")
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.munzenberger.feed.sample.config.GenerateSamplesKt")
    applicationName = "sample-config"
}

dependencies {
    implementation(project(":core"))
}
