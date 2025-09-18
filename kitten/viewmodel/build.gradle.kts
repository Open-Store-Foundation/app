plugins {
    id("core.multiplatform")
    alias(libs.plugins.composeCompiler)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.kitten.api)
                implementation(libs.kmp.androidx.viewmodel)
            }
        }
    }
}

android {
    buildFeatures {
        compose = true
    }
}
