plugins {
    id("core.multiplatform")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

compose.resources {
    packageOfResClass = "foundation.openstore.avoir.generated.resources"
}

kotlin {
    androidLibrary {
        namespace = "foundation.openstore.avoir"
        androidResources.enable = true
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.jetbrains.compose.ui.tooling.preview)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.jetbrains.compose.desktop.common)
            }
        }

        commonMain.dependencies {
            implementation(projects.core.log)
            implementation(projects.core.async)

            implementation(projects.lib.kitten.viewmodel)
            implementation(projects.lib.kitten.api)

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.icons)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.components.tooling)
            implementation(libs.jetbrains.compose.lifecycle)
            implementation(libs.jetbrains.compose.navigation)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil.compose)
        }
    }
}

dependencies {
    "androidRuntimeClasspath"(libs.android.compose.ui.tooling)
}
