import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

plugins {
    id("web.extension")
    kotlin("multiplatform")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
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
        commonMain {
            dependencies {
                implementation(libs.jetbrains.compose.viewmodel)
                implementation(projects.apps.sample.wallet.shared)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.coil.compose)
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}
