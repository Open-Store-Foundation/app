package common

import Deps
import android.AndroidVersion
import android.defaultAndroidTarget
import gradle.kotlin.dsl.accessors._5f9b97b4490741eb0c6557797296720c.androidLibrary
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

@OptIn(ExperimentalKotlinGradlePluginApi::class)
fun Project.kotlinMultiplatform(
    commonDependenices: Deps,
    targets: Set<Target> = Target.entries.toSet(),
    sourceSet: NamedDomainObjectContainer<KotlinSourceSet>.() -> Unit = {}
) {
    extensions.getByType<KotlinMultiplatformExtension>()
        .apply {
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-Xcontext-parameters")
            }

            // JVM
            if (targets.contains(Target.Desktop)) {
                jvm("desktop") {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_21)
                    }
                }
            }

            if (targets.contains(Target.Android)) {
                androidLibrary {
                    defaultAndroidTarget()
                }
            }

            // iOS
            if (targets.contains(Target.iOS)) {
                listOf(
                    iosX64(),
                    iosArm64(),
                    iosSimulatorArm64()
                ).forEach { iosTarget ->
                    iosTarget.binaries.framework {
                        baseName = publishNamespace()
                    }
                }
            }

            // JS
            if (targets.contains(Target.Js)) {
                js {
                    outputModuleName.set(jsNamespace("osf-"))

                    browser {
                        commonWebpackConfig {
                            configDirectory = rootDir.resolve("res/webpack.config.d")
                        }

                        testTask {
                            useKarma {
                                useConfigDirectory(rootDir.resolve("res/karma.config.d"))
                                useChromeHeadless()
                            }
                        }
                    }

                    useCommonJs()
                }
            }

            applyDefaultHierarchyTemplate {
                common {
                    group("commonAllJvm") {
                        if (targets.contains(Target.Desktop)) {
                            withJvm()
                        }
                    }
                }
            }

            sourceSets.apply {
                all {
                    languageSettings {
                        optIn("kotlin.js.ExperimentalJsExport")
                        optIn("kotlinx.serialization.ExperimentalSerializationApi")
                        optIn("kotlin.concurrent.atomics.ExperimentalAtomicApi")
                        optIn("kotlin.time.ExperimentalTime")
                        optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                        optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                    }
                }

                if (targets.contains(Target.Android)) {
                    val commonAllJvm = getByName("commonAllJvmMain")
                    androidMain.get()
                        .dependsOn(commonAllJvm)
                }

                commonMain.dependencies {
                    implementation(commonDependenices.coroutines())
                }

                commonTest.dependencies {
                    implementation(commonDependenices.coroutinesTest())
                    implementation(commonDependenices.kotlinTest())
                }

                sourceSet()
            }
        }
}