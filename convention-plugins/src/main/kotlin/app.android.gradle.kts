import android.androidConfig
import android.applyProjectCommon
import android.applyAndroidCommon

plugins {
    id("com.android.application")
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val androidConf = androidConfig(libs)

applyProjectCommon()

android {
    applyAndroidCommon(
        namespace = "com.openstore.app",
        versions = androidConf.appVersions,
        applicationId = "foundation.openstore.android",
    )

    buildFeatures {
        compose = true
    }
}
