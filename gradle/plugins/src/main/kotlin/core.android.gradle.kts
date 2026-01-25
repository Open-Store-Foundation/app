@file:Suppress("UnstableApiUsage")

import common.kotlinMultiplatform
import common.Target
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
    .named("libs")

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

val deps = Deps(libs)

kotlinMultiplatform(
    commonDependenices = deps,
    targets = setOf(Target.Desktop, Target.Android)
)
