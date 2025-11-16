import android.AndroidVersion
import android.androidConfig
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import common.applyProjectCommon

plugins {
    id("com.android.application")
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val androidConf = androidConfig(libs)

applyProjectCommon()

android {
    applyAndroidCommonBase(androidConf.appVersions)

    buildFeatures {
        compose = true
    }
}

private fun BaseExtension.applyAndroidCommonBase(
    versions: AndroidVersion,
    testInstrumentationRunner: String? = null,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    configSetup: DefaultConfig.() -> Unit = {},
) {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    compileSdkVersion(versions.compileSdk)
    buildToolsVersion(versions.buildTools)

    defaultConfig {
        minSdk = versions.minSdk
        targetSdk = versions.targetSdk

        manifestPlaceholders(manifestPlaceholders)
        testInstrumentationRunner?.let {
            setTestInstrumentationRunner(it)
        }

        configSetup()
    }
}
