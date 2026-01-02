plugins {
    id("core.multiplatform")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.runtime)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "foundation.openstore.app.generated.resources"
}
