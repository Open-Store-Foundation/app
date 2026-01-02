package android

import org.gradle.api.artifacts.VersionCatalog

data class AndroidVersion(
    val minSdk: Int,
    val compileSdk: Int,
    val targetSdk: Int,
    val buildTools: String,
)

data class LibVersions(
    val composeVersion: String,
    val desugaringVersion: String
)

data class AndroidConfiguration(
    val appVersions: AndroidVersion,
    val libVersions: LibVersions
)

fun androidConfig(libs: VersionCatalog): AndroidConfiguration {
    return AndroidConfiguration(
        appVersions = AndroidVersion(
            minSdk = libs.findVersion("android-sdk-min").get().requiredVersion.toInt(),
            compileSdk = libs.findVersion("android-sdk-compile").get().requiredVersion.toInt(),
            targetSdk = libs.findVersion("android-sdk-target").get().requiredVersion.toInt(),
            buildTools = libs.findVersion("android-sdk-tools").get().requiredVersion
        ),
        libVersions = LibVersions(
            composeVersion = "1",
            desugaringVersion = "1"
        )
    )
}

