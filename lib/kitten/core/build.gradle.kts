plugins {
    id("core.multiplatform")
    id("core.publishing")
}

publishingConfig {
    group = "foundation.openstore.kitten"
    artifactId = "core"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.lib.kitten.api)
            }
        }


        commonTest {
            dependencies {
                implementation(projects.lib.kitten.testCore)
            }
        }
    }
}
