import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool
import java.util.Properties

plugins {
    id("app.android")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val props = Properties().apply {
    val localPropertiesFile = rootDir.resolve("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

kotlin {
    android {
        namespace = "foundation.openstore.signer.app"

        defaultConfig {
            applicationId = "foundation.openstore.signer.android"

            val _versionCode = rootProject.findProperty("versionCode")?.toString()?.toInt() ?: 1000000000
            val _versionName = rootProject.findProperty("versionName")?.toString() ?: "0.1"

            println("Actual ANDROID app version - $_versionName ($_versionCode)")

            versionCode = _versionCode
            versionName = _versionName
        }

        buildFeatures {
            buildConfig = true
            resValues = true
        }

        packaging {
            resources {
                excludes += setOf(
                    "META-INF/*.md",
                )
            }
        }

        signingConfigs {
            if (props.keys.contains("foundation.openstore.signer.key.debug.keystore")) {
                getByName("debug") {
                    storeFile = File(props["foundation.openstore.signer.key.debug.keystore"]!!.toString())
                    storePassword = props["foundation.openstore.signer.key.debug.keystore_pass"]!!.toString()

                    keyAlias = props["foundation.openstore.signer.key.debug.alias"]!!.toString()
                    keyPassword = props["foundation.openstore.signer.key.debug.alias_pass"]!!.toString()
                }
            }

            if (props.keys.contains("foundation.openstore.signer.key.release.keystore")) {
                getByName("release") {
                    storeFile = File(props["foundation.openstore.signer.key.release.keystore"]!!.toString())
                    storePassword = props["foundation.openstore.signer.key.release.keystore_pass"]!!.toString()

                    keyAlias = props["foundation.openstore.signer.key.release.alias"]!!.toString()
                    keyPassword = props["foundation.openstore.signer.key.release.alias_pass"]!!.toString()
                }
            }
        }

        buildTypes {
            debug {
                resValue("string", "app_name", "Firebox Dev")
                applicationIdSuffix = ".dev"
                isDebuggable = true
                isMinifyEnabled = false
                isShrinkResources = false
                isCrunchPngs = false
                signingConfig = signingConfigs.getByName("debug")
            }

            release {
                isDebuggable = false
                isMinifyEnabled = true
                isShrinkResources = true
                isCrunchPngs = true

                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }

        lint {
            // if true, only report errors.
            disable += "MissingTranslation"
            ignoreTestSources = true
            checkReleaseBuilds = false
            abortOnError = false
        }

        testBuildType = "debug"
    }

    dependencies {
        implementation(libs.jetbrains.compose.ui.tooling.preview)
        implementation(libs.android.splash)
        implementation(libs.android.appcompat)
        implementation(projects.apps.signer.app.shared)
    }
}
