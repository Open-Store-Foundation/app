plugins {
    id("core.multiplatform")
    alias(libs.plugins.composeCompiler)
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
