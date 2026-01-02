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
                implementation("dev.whyoleg.cryptography:cryptography-provider-optimal:0.5.0")
                implementation("dev.whyoleg.cryptography:cryptography-random:0.5.0")
            }
        }
    }
}
