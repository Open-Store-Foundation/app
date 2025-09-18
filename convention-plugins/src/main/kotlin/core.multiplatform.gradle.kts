@file:Suppress("UnstableApiUsage")

import android.androidConfig
import android.androidLibrary
import common.kotlinMultiplatform

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val androidConf = androidConfig(libs)

kotlinMultiplatform(
    deps = Deps(libs)
)

androidLibrary(
    versions = androidConf.appVersions,
)
