import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

plugins {
    id("app.android")
    id("web.extension")
    kotlin("multiplatform")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedWallet"
            isStatic = true
        }
    }

    js(IR) {
        binaries.executable()

        browser {
            commonWebpackConfig {
                outputFileName = "os-wallet-sample.js"
//                sourceMaps = true

                if (mode == KotlinWebpackConfig.Mode.DEVELOPMENT) {
                    devtool = WebpackDevtool.INLINE_SOURCE_MAP
                }
            }
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.ui.tooling.preview)
                implementation(libs.android.lifecycle.process)
                implementation(libs.android.appcompat)
                implementation(libs.android.permissions)
                implementation(libs.android.compose.activity)

                implementation(projects.core.log)
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(projects.core.store)
                implementation(projects.core.common)
                implementation(projects.lib.avoir)

                implementation(projects.lib.mvi)

                implementation(projects.lib.kitten.api)
                implementation(projects.lib.kitten.core)
                implementation(projects.lib.kitten.viewmodel)

                api(projects.lib.gcip.core)
                api(projects.lib.gcip.platform)
                api(projects.lib.gcip.encryption)
                api(projects.lib.gcip.sdk)

                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.icons)
                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.components.resources)

                implementation(libs.settings)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktor.json)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.coil.compose)
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}

android {
    namespace = "com.openwallet.example.app"

    defaultConfig {
        applicationId = "foundation.openwallet.example"
        versionCode = 1
        versionName = "1.0"
    }
}
