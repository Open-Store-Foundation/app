package common

import org.gradle.api.Project

fun Project.jsNamespace(prefix: String): String =
    prefix + path.trim(':')
        .replace('-', '_')
        .replace(':', '-')

fun Project.publishNamespace(): String =
    path.trim(':')
        .replace(":", "-")
