package common

import Deps
import android.AndroidVersion
import android.defaultAndroidTarget
import gradle.kotlin.dsl.accessors._d331aa179b38a7f1ef6e9c1372f179d9.androidLibrary
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

@OptIn(ExperimentalKotlinGradlePluginApi::class)
fun Project.kotlinMultiplatform(
    commonDependenices: Deps,
    androidVersions: AndroidVersion,
    sourceSet: NamedDomainObjectContainer<KotlinSourceSet>.() -> Unit = {}
) {
    extensions.getByType<KotlinMultiplatformExtension>()
        .apply {
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-Xcontext-parameters")
            }

            // JVM
            jvm("desktop") {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }

            androidLibrary {
                defaultAndroidTarget(this, androidVersions)
            }

            // iOS
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = publishNamespace()
                }
            }

            // JS
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

            applyDefaultHierarchyTemplate {
                common {
                    group("commonAllJvm") {
                        withJvm()
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

                val commonAllJvm = getByName("commonAllJvmMain")
                androidMain.get()
                    .dependsOn(commonAllJvm)

                commonMain.dependencies {
                    implementation(commonDependenices.coroutines())
                }

                commonTest.dependencies {
                    implementation(commonDependenices.coroutinesTest())
                    implementation(commonDependenices.kotlinTest())
                }
            }
        }
}