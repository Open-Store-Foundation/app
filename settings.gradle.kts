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
    rootDir.resolve("gradle/plugins"),
)

rootProject.name = "OpenStore"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    /////////////////////////////// APPS
    // Store
    ":apps:store:app",
    ":apps:store:core:strings",
    ":apps:store:core:installer",
    ":apps:store:core:cert",
    ":apps:store:feature:catalog",

    // Signer
    ":apps:signer:app",
    ":apps:signer:data",

    // Wallet Sample
    ":apps:sample:wallet",

    /////////////////////////////// LIBS
    // UI
    ":lib:avoir",

    // GCIP
    ":lib:gcip:core",
    ":lib:gcip:platform",
    ":lib:gcip:transform",
    ":lib:gcip:encryption",
    ":lib:gcip:signing",
    ":lib:gcip:sdk",

    // DI
    ":lib:kitten:api",
    ":lib:kitten:core",
    ":lib:kitten:viewmodel",

    // MVI
    ":lib:mvi",

    /////////////////////////////// COMMON
    ":core:net",
    ":core:net:json-rpc",
    ":core:net:core",

    ":core:os",
    ":core:common",
    ":core:config",
    ":core:log",
    ":core:async",
    ":core:root",
    ":core:store",
)
