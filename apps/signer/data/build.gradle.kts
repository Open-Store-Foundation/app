plugins {
    id("core.multiplatform")
    id("app.cash.sqldelight") version "2.2.1"
    alias(libs.plugins.kotlinSerialization)
}

sqldelight {
    databases {
        create("SignerDatabase") {
            packageName.set("foundation.openstore.signer.app.data.dao")
            generateAsync.set(true)
        }
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.common)
                implementation(projects.lib.gcip.core)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation("app.cash.sqldelight:coroutines-extensions:2.2.1")
            }
        }
        
        val androidMain by getting {
            dependencies {
//                implementation(libs.sqldelight.driver.android)
                implementation("app.cash.sqldelight:android-driver:2.2.1")
            }
        }
        
        val iosMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:native-driver:2.2.1")
            }
        }
        
        getByName("jsMain") {
            dependencies {
                implementation("app.cash.sqldelight:web-worker-driver:2.2.1")
            }
        }
    }
}
