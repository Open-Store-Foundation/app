import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.gradle.ComposeHotRun
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool
import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("app.android")
    id("web.extension")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
}

val props = Properties().apply {
    val localPropertiesFile = rootDir.resolve("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget()

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "OsSignerLib"
            isStatic = true
            export(projects.lib.gcip.platform)
            export(projects.lib.gcip.encryption)
        }
    }

    js(IR) {
        outputModuleName = "SignerJs"
        browser {
            commonWebpackConfig {
                outputFileName = "signer-js.js"
                sourceMaps = true

                if (mode == KotlinWebpackConfig.Mode.DEVELOPMENT) {
                    devtool = WebpackDevtool.INLINE_SOURCE_MAP
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.android.splash)
            implementation(libs.android.appcompat)
        }

        jsMain.dependencies {
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.2.1"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(devNpm("copy-webpack-plugin", "11.0.0"))
        }

        commonMain.dependencies {
            implementation(projects.lib.avoir)
            implementation(projects.core.log)
            implementation(projects.lib.mvi)
            implementation(projects.core.async)
            implementation(projects.core.config)
            implementation(projects.core.store)
            implementation(projects.core.common)
            implementation(projects.core.root)
            implementation(projects.core.os)

            implementation(projects.apps.signer.data)
//            implementation(projects.apps.signer.core.cryptography)

            api(projects.lib.gcip.transform)
            api(projects.lib.gcip.core)
            api(projects.lib.gcip.platform)
            api(projects.lib.gcip.encryption)
            api(projects.lib.gcip.signing)

            implementation(projects.lib.kitten.viewmodel)
            implementation(projects.lib.kitten.core)
            implementation(projects.lib.kitten.api)

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.icons)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.components.tooling)

            implementation(libs.jetbrains.compose.viewmodel)
            implementation(libs.jetbrains.compose.navigation)

            implementation(libs.kotlinx.datetime)
            implementation(libs.coil.compose)
        }
    }
}

compose.resources {
//    publicResClass = true
//    generateResClass = auto
    packageOfResClass = "foundation.openstore.signer.app.generated.resources"
}

// DESKTOP
compose.desktop {
    application {
        mainClass = "foundation.openstore.signer.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Firebox"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register<ComposeHotRun>("runHot") {
    mainClass.set("foundation.openstore.signer.app.MainKt")
}

// ANDROID
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

            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
