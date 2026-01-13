import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish")
}

val config = extensions.create<PublishingConfig>("publishingConfig")

val targetMapper = { name: String ->
    when {
        name.endsWith("-desktop") -> name.replace("-desktop", "-jvm")
        else -> name
    }
}

val isPublishing = System.getenv("PUBLISHING")?.ifBlank { null } != null

if (isPublishing) {
    afterEvaluate {
        mavenPublishing {
            publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
            signAllPublications()

            val groupId = config.group
                ?: throw IllegalStateException("group is required")

            val artifactId = config.artifactId
                ?: throw IllegalStateException("artifactId is required")

            val version = config.version
                ?: System.getenv("REPO_VERSION")
                    ?.ifBlank { null }
                ?: throw IllegalStateException("REPO_VERSION is required")

            coordinates(groupId, artifactId, version)

            pom {
                val repoPath = System.getenv("REPO_PATH")
                    ?.ifBlank { null }
                    ?: throw IllegalStateException("REPO_PATH is required")

                val repoName = System.getenv("REPO_NAME")
                    ?.ifBlank { null }
                    ?: throw IllegalStateException("REPO_NAME is required")

                val repoDescription = System.getenv("REPO_DESCRIPTION")
                    ?.ifBlank { null }
                    ?: throw IllegalStateException("REPO_DESCRIPTION is required")

                name.set(repoName)
                description.set(repoDescription)
                inceptionYear.set("2026")
                url.set("https://$repoPath")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Open-Store-Foundation")
                        name.set("Open Store Foundation")
                        url.set("https://github.com/Open-Store-Foundation/")
                    }
                }
                scm {
                    url.set("https://$repoPath")
                }
            }
        }

        publishing {
            publications.withType<MavenPublication> {
                artifactId = targetMapper(artifactId)
            }
        }
    }
}
