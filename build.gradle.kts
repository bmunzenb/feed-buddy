import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("feed-buddy.kotlin-conventions")
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r|-jre|-android)?$".toRegex()
    return !(stableKeyword || regex.matches(this))
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    rejectVersionIf {
        candidate.version.isNonStable() && !currentVersion.isNonStable()
    }
}
