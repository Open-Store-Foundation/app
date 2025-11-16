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

                implementation("dev.whyoleg.cryptography:cryptography-provider-optimal:0.5.0")
                implementation("dev.whyoleg.cryptography:cryptography-random:0.5.0")
                
                implementation(project.dependencies.platform("org.kotlincrypto.hash:bom:0.8.0"))
                implementation(project.dependencies.platform("org.kotlincrypto.macs:bom:0.8.0"))
                implementation("org.kotlincrypto.hash:sha2")
                implementation("org.kotlincrypto.hash:sha3")
                implementation("org.kotlincrypto.hash:blake2")
                implementation("org.kotlincrypto.macs:hmac-sha2")
            }
        }
    }
}
