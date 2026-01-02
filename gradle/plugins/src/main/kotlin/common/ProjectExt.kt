package common

import com.android.build.gradle.tasks.factory.AndroidUnitTest
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.collections.plus

fun Project.jsNamespace(prefix: String): String =
    prefix + path.trim(':')
        .replace('-', '_')
        .replace(':', '-')

fun Project.publishNamespace(): String =
    path.trim(':')
        .replace(":", "-")


fun Project.applyProjectCommon() {
    tasks.withType<JavaCompile> {
        options.compilerArgs =
            options.compilerArgs + listOf("-Xlint:unchecked", "-Xlint:deprecation")
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }

    tasks.withType<AndroidUnitTest> {
        enabled = false
    }
}
