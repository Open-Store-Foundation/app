package common

import Deps
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

@OptIn(ExperimentalKotlinGradlePluginApi::class)
fun Project.kotlinMultiplatform(
    deps: Deps,
    sourceSet: NamedDomainObjectContainer<KotlinSourceSet>.() -> Unit = {}
) {
    extensions.getByType<KotlinMultiplatformExtension>()
        .apply {
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-Xcontext-receivers")
            }

            // JVM
            androidTarget {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }

                instrumentedTestVariant {
                    sourceSetTree.set(KotlinSourceSetTree.test)
                }
            }

            jvm("desktop") {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }

            applyDefaultHierarchyTemplate {
                common {
                    group("commonAllJvm") {
                        withAndroidTarget()
                        withJvm()
                    }
                }
            }

            // iOS
//            listOf(
//                iosX64(),
//                iosArm64(),
//                iosSimulatorArm64()
//            ).forEach { iosTarget ->
//                iosTarget.binaries.framework {
//                    baseName = publishNamespace()
//                }
//            }

            // JS
            js {
                outputModuleName.set(jsNamespace("open-wallet-"))

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

                commonMain.dependencies {
                    implementation(deps.coroutines())
                }

                commonTest.dependencies {
                    implementation(kotlin("test"))
                    implementation(deps.coroutinesTest())
                }

                androidInstrumentedTest.dependencies {
                    implementation(deps.androidTestRunner())
                }
            }
        }
}