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
    }
}

//android {
//    buildFeatures {
//        compose = true
//    }
//}
