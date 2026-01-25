@file:Suppress("UnstableApiUsage")

import common.kotlinMultiplatform

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
    .named("libs")

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

val deps = Deps(libs)

kotlinMultiplatform(
    commonDependenices = deps,
)
