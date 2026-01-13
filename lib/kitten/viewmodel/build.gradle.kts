plugins {
    id("core.multiplatform")
    id("core.publishing")
    alias(libs.plugins.composeCompiler)
}

publishingConfig {
    group = "foundation.openstore.kitten"
    artifactId = "viewmodel"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.lib.kitten.api)
                implementation(libs.jetbrains.compose.viewmodel)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.lib.kitten.testCore)
                implementation(projects.lib.kitten.core)

                implementation(libs.jetbrains.compose.lifecycle)
            }
        }
    }
}
