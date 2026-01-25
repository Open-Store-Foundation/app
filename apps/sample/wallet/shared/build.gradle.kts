plugins {
    id("core.multiplatform")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "SharedWallet"
//            isStatic = true
//        }
//    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.store)
                api(projects.core.common)
                api(projects.lib.avoir)

                api(projects.lib.mvi)

                api(projects.lib.kitten.api)
                api(projects.lib.kitten.core)
                api(projects.lib.kitten.viewmodel)

                api(projects.lib.gcip.core)
                api(projects.lib.gcip.platform)
                api(projects.lib.gcip.encryption)
                api(projects.lib.gcip.sdk)

                api(libs.jetbrains.compose.runtime)
                api(libs.jetbrains.compose.viewmodel)
                api(libs.jetbrains.compose.foundation)
                api(libs.jetbrains.compose.material3)
                api(libs.jetbrains.compose.icons)
                api(libs.jetbrains.compose.ui)
                api(libs.jetbrains.compose.components.resources)

                api(libs.settings)
                api(libs.kotlinx.datetime)
                api(libs.ktor.json)
            }
        }
    }
}
