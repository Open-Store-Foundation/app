plugins {
    id("compose.multiplatform")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.android.compose.ui.tooling.preview)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }

        commonMain.dependencies {
            implementation(projects.core.strings)
            implementation(projects.core.strings)
            implementation(projects.core.log)
            implementation(projects.core.mvi)
            implementation(projects.core.async)

            implementation(projects.kitten.viewmodel)
            implementation(projects.kitten.api)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.compose.navigation)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.coil.compose)
        }
    }
}

dependencies {
    debugImplementation(libs.android.compose.ui.tooling)
}
