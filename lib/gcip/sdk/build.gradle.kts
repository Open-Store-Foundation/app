plugins {
    alias(libs.plugins.kotlinSerialization)
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(projects.lib.gcip.core)
                implementation(projects.lib.gcip.platform)
                implementation(projects.lib.gcip.encryption)
                implementation(projects.lib.gcip.transform)
            }
        }
    }
}
