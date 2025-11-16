plugins {
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.lib.gcip.core)
                implementation(libs.kotlinx.serialization.cbor)
            }
        }
    }
}
