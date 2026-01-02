plugins {
    id("core.multiplatform")
}

kotlin {
    androidLibrary {
        androidResources.enable = true
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.android.startup)
                implementation(libs.android.core)
            }
        }
    }
}

