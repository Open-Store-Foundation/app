import android.AndroidVersion
import android.androidDefaultConfig
import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import common.applyProjectCommon

plugins {
    id("com.android.application")
}

applyProjectCommon()

android {
    applyAndroidApp()

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

private fun ApplicationExtension.applyAndroidApp(
    versions: AndroidVersion = androidDefaultConfig().appVersions,
    testRunner: String? = null,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    configSetup: ApplicationDefaultConfig.() -> Unit = {},
) {
    compileSdk = versions.compileSdk
    buildToolsVersion = versions.buildTools

    defaultConfig {
        minSdk = versions.minSdk
        targetSdk = versions.targetSdk

        addManifestPlaceholders(manifestPlaceholders)
        testRunner?.let {
            testInstrumentationRunner = it
        }

        configSetup()
    }
}
