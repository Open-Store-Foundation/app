@file:Suppress("OPT_IN_USAGE")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.core.os)
                implementation(projects.core.async)
                implementation(projects.core.common)
                implementation(projects.core.store)
                implementation(projects.kitten.api)
                implementation(projects.kitten.viewmodel)

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
