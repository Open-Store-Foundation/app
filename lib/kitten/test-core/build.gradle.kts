plugins {
    id("core.multiplatform")
    id("core.publishing")
}

publishingConfig {
    group = "foundation.openstore.kitten"
    artifactId = "test-core"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.lib.kitten.api)
                implementation(projects.lib.kitten.core)
            }
        }
    }
}
