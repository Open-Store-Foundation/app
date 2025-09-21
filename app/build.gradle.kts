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
    id("org.jetbrains.compose.hot-reload") version "1.0.0-beta07"
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }

        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "OpenStore"
//            isStatic = true
//        }
//    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.android.compose.ui.tooling.preview)
            implementation(libs.android.lifecycle.process)
            implementation(libs.android.splash)
            implementation(libs.android.sappcompat)
            implementation(libs.android.permissions)
            implementation(projects.core.net.core)
            implementation(projects.core.log)
            implementation(projects.core.os)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
            }
        }

        commonMain.dependencies {
            implementation(libs.cryptohash)

            implementation(projects.ui)
            implementation(projects.installer)
            implementation(projects.core.strings)
            implementation(projects.core.log)
            implementation(projects.core.mvi)
            implementation(projects.core.async)
            implementation(projects.core.net.core)
            implementation(projects.core.net.jsonRpc)
            implementation(projects.core.config)
            implementation(projects.core.store)
            implementation(projects.core.common)

            implementation(projects.kitten.viewmodel)
            implementation(projects.kitten.core)
            implementation(projects.kitten.api)

            implementation(projects.features.catalog)

            implementation(projects.library)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

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

            implementation(libs.compose.navigation)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
//    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
//    add("kspIosX64", libs.androidx.room.compiler)
//    add("kspIosArm64", libs.androidx.room.compiler)
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

composeCompiler {

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
    defaultConfig {
        val _versionCode = rootProject.property("versionCode")?.toString()?.toInt() ?: 1000000000
        val _versionName = rootProject.property("versionName")?.toString() ?: "0.1"

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
        if (props.keys.contains("foundation.openstore.key.debug.keystore")) {
            getByName("debug") {
                storeFile = File(props["foundation.openstore.key.debug.keystore"]!!.toString())
                storePassword = props["foundation.openstore.key.debug.keystore_pass"]!!.toString()

                keyAlias = props["foundation.openstore.key.debug.alias"]!!.toString()
                keyPassword = props["foundation.openstore.key.debug.alias_pass"]!!.toString()
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
