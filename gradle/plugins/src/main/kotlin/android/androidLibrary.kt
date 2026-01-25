package android

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import common.applyProjectCommon
import org.gradle.api.Project

context(target: KotlinMultiplatformAndroidLibraryTarget)
fun Project.defaultAndroidTarget(
    versions: AndroidVersion = androidDefaultConfig().appVersions,
    namespace: String = "foundation.openstore.app",
    withResources: Boolean = false,
    setup: KotlinMultiplatformAndroidLibraryExtension.() -> Unit = {},
) {
    applyProjectCommon()

    target.apply {
        applyAndroidCommon(
            androidNamespace(namespace),
            versions,
        )

        androidResources.enable = withResources

        setup()
    }
}

fun Project.androidNamespace(commonPackage: String): String =
    commonPackage + path.trim(':')
        .replace(':', '.')
        .replace("-", "")


fun KotlinMultiplatformAndroidLibraryTarget.applyAndroidCommon(
    namespace: String,
    versions: AndroidVersion,
) {
    this.namespace = namespace
    applyAndroidCommonBase(versions)
}

fun KotlinMultiplatformAndroidLibraryTarget.applyAndroidCommonBase(
    versions: AndroidVersion,
) {
    minSdk = versions.minSdk
    compileSdk = versions.compileSdk
    buildToolsVersion = versions.buildTools
}
