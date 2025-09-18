@file:Suppress("UnstableApiUsage")

import java.util.Properties

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootDir.resolve("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

fun getProperty(key: String): String {
    return localProperties.getProperty(key)
        ?: throw GradleException("Key `$key` is undefine, Please add it to local.properties!",)
}

dependencyResolutionManagement {
    // Uncomment after https://youtrack.jetbrains.com/issue/KT-55620/
    // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        gradlePluginPortal()
    }
}

buildCache {
    //https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_configure_remote
    local {
        isEnabled = true
        directory = rootDir.resolve(".build-cache")
    }
}

includeBuild(
    "convention-plugins",
)

rootProject.name = "OpenStore"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":app",
    ":library",
    ":ui",
    ":installer",

    ":kitten:api",
    ":kitten:core",
    ":kitten:viewmodel",

    ":features:catalog",

    ":core",
    ":core:common",
    ":core:os",
    ":core:store",
    ":core:net",
    ":core:net:json-rpc",
    ":core:net:core",

    ":core:config",
    ":core:log",
    ":core:mvi",
    ":core:strings",
    ":core:async",
)
