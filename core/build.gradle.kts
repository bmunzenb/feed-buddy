plugins {
    id("feed-buddy.kotlin-conventions")
    `java-library`
    `maven-publish`
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.commons.email)
    implementation(libs.commons.text)
    implementation(libs.velocity)
    implementation(libs.okio)
    implementation(libs.jackson)
    implementation(libs.jackson.xml)
    implementation(libs.jackson.yaml)
    implementation(libs.jackson.kotlin)
    implementation(libs.woodstox)
    implementation(libs.slf4j.noop) // needed to prevent messages from velocity

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "${rootProject.name}-${project.name}",
            "Implementation-Version" to project.version,
        )
    }
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bmunzenb/feed-buddy")
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = "${rootProject.name}-${project.name}"

            pom {
                name = "Feed Buddy Core"
                description = "RSS and Atom feed processor"
                url = "https://github.com/bmunzenb/feed-buddy"
                licenses {
                    license {
                        name = "Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }
                scm {
                    url = "https://github.com/bmunzenb/feed-buddy"
                }
            }

            from(components["java"])
        }
    }
}
