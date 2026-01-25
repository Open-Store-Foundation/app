@file:Suppress("OPT_IN_USAGE")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("core.multiplatform")
}

kotlin {
    androidLibrary {
        androidResources.enable = true
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.core.os)
                implementation(projects.core.async)
                implementation(projects.core.common)
                implementation(projects.core.store)
                implementation(projects.lib.kitten.api)
                implementation(projects.lib.kitten.core)
                implementation(projects.lib.kitten.viewmodel)

                implementation(libs.cryptohash)
                implementation(libs.android.core)
                implementation(libs.android.coreold)
                implementation(libs.android.compose.activity)
                implementation(libs.android.lifecycle.service)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.log)
                implementation(libs.ktor.core)
            }
        }
    }
}
