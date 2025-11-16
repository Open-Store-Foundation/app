@file:Suppress("UnstableApiUsage")

import android.androidConfig
import common.kotlinMultiplatform

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
    .named("libs")

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

val androidConf = androidConfig(libs)
val deps = Deps(libs)

kotlinMultiplatform(
    commonDependenices = deps,
    androidVersions = androidConf.appVersions,
)

