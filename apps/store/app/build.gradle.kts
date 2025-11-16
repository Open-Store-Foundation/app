import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.gradle.ComposeHotRun
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.Properties

plugins {
    id("app.android")
    kotlin("multiplatform")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget()

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.android.lifecycle.process)
            implementation(libs.android.splash)
            implementation(libs.android.appcompat)
            implementation(libs.android.permissions)
            implementation(projects.core.net.core)
            implementation(projects.core.log)
            implementation(projects.core.os)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.jetbrains.compose.desktop.common)
                implementation(compose.desktop.currentOs)
            }
        }

        commonMain.dependencies {
            implementation(libs.cryptohash)

            implementation(projects.lib.avoir)

            implementation(projects.lib.kitten.viewmodel)
            implementation(projects.lib.kitten.core)
            implementation(projects.lib.kitten.api)

            implementation(projects.core.log)
            implementation(projects.lib.mvi)
            implementation(projects.core.async)
            implementation(projects.core.net.core)
            implementation(projects.core.net.jsonRpc)
            implementation(projects.core.config)
            implementation(projects.core.store)
            implementation(projects.core.common)

            implementation(projects.apps.store.core.cert)
            implementation(projects.apps.store.core.installer)
            implementation(projects.apps.store.core.strings)
            implementation(projects.apps.store.feature.catalog)

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.icons)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.components.tooling)

            implementation(libs.bignum)

            implementation(libs.androidx.room.runtime)

            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.core)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.json)
            implementation(libs.ktor.logging)

            implementation(libs.coil.compose)
            implementation(libs.coil.svg)
            implementation(libs.coil.ktor)

            implementation(libs.jetbrains.compose.navigation)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}

compose.resources {
    publicResClass = true
    generateResClass = always
}

room {
    schemaDirectory("$projectDir/schemas")
}

// DESKTOP
compose.desktop {
    application {
        mainClass = "com.openstore.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "OpenStore"
            packageVersion = "1.0.0"

            // windows { ... }
            // macos { ... }
            // linux { ... }

            // includeAllModules = true
        }
    }
}

tasks.register<ComposeHotRun>("runHot") {
    mainClass.set("com.openstore.app.MainKt")
}

// ANDROID
dependencies {
    debugImplementation(libs.android.compose.ui.tooling)
}

val props = Properties().apply {
    val localPropertiesFile = rootDir.resolve("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.openstore.app"

    defaultConfig {
        applicationId = "foundation.openstore.android"

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
        if (props.keys.contains("foundation.openstore.store.key.debug.keystore")) {
            getByName("debug") {
                storeFile = File(props["foundation.openstore.store.key.debug.keystore"]!!.toString())
                storePassword = props["foundation.openstore.store.key.debug.keystore_pass"]!!.toString()

                keyAlias = props["foundation.openstore.store.key.debug.alias"]!!.toString()
                keyPassword = props["foundation.openstore.store.key.debug.alias_pass"]!!.toString()
            }
        }

        if (props.keys.contains("foundation.openstore.store.key.release.keystore")) {
            getByName("release") {
                storeFile = File(props["foundation.openstore.store.key.release.keystore"]!!.toString())
                storePassword = props["foundation.openstore.store.key.release.keystore_pass"]!!.toString()

                keyAlias = props["foundation.openstore.store.key.release.alias"]!!.toString()
                keyPassword = props["foundation.openstore.store.key.release.alias_pass"]!!.toString()
            }
        }
    }

    buildTypes {
        debug {
            resValue("string", "app_name", "Open Store Dev")
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
