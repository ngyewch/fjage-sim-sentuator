plugins {
    `java-library`
    `maven-publish`
    signing
    id("ca.cutterslade.analyze") version "1.7.1"
    id("com.asarkar.gradle.build-time-tracker") version "3.0.1"
    id("com.github.ben-manes.versions") version "0.39.0"
}

project.group = "com.github.ngyewch.fjage-sim-sentuator"
project.version = "0.1.0"

val isReleaseVersion = !(project.version as String).endsWith("SNAPSHOT")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("com.github.org-arl:fjage-sentuator:1.2.1")
    runtimeOnly("com.twelvemonkeys.imageio:imageio-pnm:3.7.0")
    compileOnly("org.jetbrains:annotations:21.0.1")

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter:5.7.2")
}

repositories {
    mavenCentral()
}

tasks {
    withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        gradleReleaseChannel = "current"

        fun isNonStable(version: String): Boolean {
            val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
            val regex = "^[0-9,.v-]+(-r)?$".toRegex()
            val isStable = stableKeyword || regex.matches(version)
            return isStable.not()
        }

        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
            )
        }
    }

    withType(Sign::class) {
        onlyIf { isReleaseVersion }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            from(components["java"])

            pom {
                name.set("fjage-sim-sentuator")
                description.set("fjage simulated sentuators.")
                url.set("https://github.com/ngyewch/fjage-sim-sentuator")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/ngyewch/fjage-sim-sentuator/blob/main/LICENSE")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:ngyewch/fjage-sim-sentuator.git")
                    developerConnection.set("scm:git:git@github.com:ngyewch/fjage-sim-sentuator.git")
                    url.set("https://github.com/ngyewch/fjage-sim-sentuator")
                }
                developers {
                    developer {
                        id.set("ngyewch")
                        name.set("Nick Ng")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            if (isReleaseVersion) {
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            } else {
                setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            }
            credentials {
                val ossrhUsername: String? by project
                val ossrhPassword: String? by project
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
