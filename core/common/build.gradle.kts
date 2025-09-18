plugins {
    alias(libs.plugins.kotlinSerialization)
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.bignum)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
