plugins {
    id("feed-buddy.kotlin-conventions")
    application
}

application {
    mainClass.set("com.munzenberger.feed.app.AppKt")
    applicationName = "feed-buddy"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.clikt)
}
