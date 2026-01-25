import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.gradle.ComposeHotRun
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("core.android")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidLibrary {
        androidResources.enable = true
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(libs.jetbrains.compose.desktop.common)
//                implementation(compose.desktop.currentOs)
            }
        }

        commonMain.dependencies {
            api(libs.cryptohash)

            api(projects.lib.avoir)

            api(projects.lib.kitten.viewmodel)
            api(projects.lib.kitten.core)
            api(projects.lib.kitten.api)

            api(projects.core.log)
            api(projects.lib.mvi)
            api(projects.core.async)
            api(projects.core.net.core)
            api(projects.core.net.jsonRpc)
            api(projects.core.config)
            api(projects.core.store)
            api(projects.core.common)

            api(projects.apps.store.core.cert)
            api(projects.apps.store.core.installer)
            api(projects.apps.store.core.strings)
            api(projects.apps.store.feature.catalog)

            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.icons)
            api(libs.jetbrains.compose.ui)
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.components.tooling)

            api(libs.bignum)

            api(libs.androidx.room.runtime)

            api(libs.kotlinx.datetime)
            api(libs.ktor.core)
            api(libs.ktor.content.negotiation)
            api(libs.ktor.json)
            api(libs.ktor.logging)

            api(libs.coil.compose)
            api(libs.coil.svg)
            api(libs.coil.ktor)

            api(libs.jetbrains.compose.navigation)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}

// DESKTOP
compose.desktop {
    application {
        mainClass = "com.openstore.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "OpenStore"
            packageVersion = "1.0.0"

            // windows {  }
            // macos {  }
            // linux {  }
            // includeAllModules = true
        }
    }
}

tasks.register<ComposeHotRun>("runHot") {
    mainClass.set("com.openstore.app.MainKt")
}
