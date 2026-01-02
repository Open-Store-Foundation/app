plugins {
    alias(libs.plugins.kotlinSerialization)
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.cbor)
            implementation(libs.kotlinx.io)
        }
    }
}
