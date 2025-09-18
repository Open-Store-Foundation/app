plugins {
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.android.startup)
                implementation(libs.android.core)
            }
        }
    }
}

