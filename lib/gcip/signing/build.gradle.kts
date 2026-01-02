import org.gradle.kotlin.dsl.projects

plugins {
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.lib.gcip.core)
            implementation(projects.lib.gcip.transform)
            implementation(libs.bignum)
            implementation("io.github.andreypfau:curve25519-kotlin:0.0.8")

            implementation("dev.whyoleg.cryptography:cryptography-provider-optimal:0.5.0")
            implementation("dev.whyoleg.cryptography:cryptography-random:0.5.0")
        }

        androidMain.dependencies {
            implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-android:0.21.0")
            implementation("fr.acinq.secp256k1:secp256k1-kmp:0.21.0")
        }

        iosMain.dependencies {
            implementation("fr.acinq.secp256k1:secp256k1-kmp:0.21.0")
        }
    }
}
