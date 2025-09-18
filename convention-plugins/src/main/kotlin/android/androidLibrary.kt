package android

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.tasks.factory.AndroidUnitTest
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

const val androidJunitRunner = "androidx.test.runner.AndroidJUnitRunner"

fun Project.androidNamespace(commonPackage: String): String =
    commonPackage + path.trim(':')
        .replace(':', '.')
        .replace("-", "")

fun Project.androidLibrary(
    namespace: String = "foundation.openstore.app",
    versions: AndroidVersion,
    testInstrumentationRunner: String = androidJunitRunner,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    setup: LibraryExtension.() -> Unit = {},
    defaultConfig: DefaultConfig.() -> Unit = {},
    withCompose: Boolean = false,
) {
    applyProjectCommon()

    extensions.getByType<LibraryExtension>()
        .apply {
            applyAndroidCommon(
                androidNamespace(namespace),
                versions,
                testInstrumentationRunner,
                manifestPlaceholders,
                defaultConfig,
            )

            @Suppress("UnstableApiUsage")
            buildFeatures {
                resValues = null
                buildConfig = false
                viewBinding = false
                compose = withCompose

                shaders = false
                prefab = false
                dataBinding = false
                mlModelBinding = false
                prefabPublishing = false
                aidl = false
                renderScript = false
            }

            sourceSets {
                val androidTest by getting
                androidTest.resources.srcDirs(projectDir.resolve("src/commonTest/resources"))
            }

            testOptions {
                animationsDisabled = true
            }

            setup()
        }
}

fun Project.applyProjectCommon() {
    tasks.withType<JavaCompile> {
        options.compilerArgs =
            options.compilerArgs + listOf("-Xlint:unchecked", "-Xlint:deprecation")
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }

    tasks.withType<AndroidUnitTest> {
        enabled = false
    }
}

@Suppress("UnstableApiUsage")
fun BaseExtension.applyAndroidCommon(
    namespace: String,
    versions: AndroidVersion,
    testInstrumentationRunner: String? = null,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    configSetup: DefaultConfig.() -> Unit = {},
    applicationId: String? = null,
) {
    this.namespace = namespace

    compileOptions {
//        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    compileSdkVersion(versions.compileSdk)
    buildToolsVersion(versions.buildTools)

    defaultConfig {
        if (applicationId != null) {
            this.applicationId = applicationId
        }

        minSdk = versions.minSdk
        targetSdk = versions.targetSdk

        manifestPlaceholders(manifestPlaceholders)
        testInstrumentationRunner?.let {
            setTestInstrumentationRunner(it)
        }

        configSetup()
    }
}
