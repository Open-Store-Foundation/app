import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.gradle.ComposeHotRun
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool
import java.util.Properties

plugins {
    id("core.multiplatform")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidLibrary {
        androidResources.enable = true
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "OsSignerLib"
//            isStatic = true
//            export(projects.lib.gcip.platform)
//            export(projects.lib.gcip.encryption)
//        }
//    }

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
        jsMain.dependencies {
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.2.1"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(devNpm("copy-webpack-plugin", "11.0.0"))
        }

        commonMain.dependencies {
            api(projects.lib.avoir)
            api(projects.core.log)
            api(projects.lib.mvi)
            api(projects.core.async)
            api(projects.core.config)
            api(projects.core.store)
            api(projects.core.common)
            api(projects.core.root)
            api(projects.core.os)

            api(projects.apps.signer.data)
//            api(projects.apps.signer.core.cryptography)

            api(projects.lib.gcip.transform)
            api(projects.lib.gcip.core)
            api(projects.lib.gcip.platform)
            api(projects.lib.gcip.encryption)
            api(projects.lib.gcip.signing)

            api(projects.lib.kitten.viewmodel)
            api(projects.lib.kitten.core)
            api(projects.lib.kitten.api)

            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.icons)
            api(libs.jetbrains.compose.ui)
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.components.tooling)

            api(libs.jetbrains.compose.viewmodel)
            api(libs.jetbrains.compose.navigation)

            api(libs.kotlinx.datetime)
            api(libs.coil.compose)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = auto
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
