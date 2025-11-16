plugins {
    alias(libs.plugins.kotlinSerialization)
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.common)
                implementation(projects.core.log)

                implementation(libs.ktor.core)
                implementation(libs.ktor.logging)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.coroutines.android)
                implementation(libs.okhttp)
            }
        }

        commonAllJvmMain {
            dependencies {
                implementation(libs.ktor.okhttp)
                implementation(libs.okhttp)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.darwin)
            }
        }

        jsMain {
            dependencies {
                implementation(projects.core.os)
                implementation(libs.ktor.js)
            }
        }
    }
}
