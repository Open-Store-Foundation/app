import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("core.multiplatform")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.config)
                implementation(projects.core.log)
                implementation(projects.core.async)
                implementation(projects.core.common)

                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.viewmodel)
                implementation(libs.androidx.annotations)
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

//android {
//    buildFeatures {
//        compose = true
//    }
//}

dependencies {
//    compileOnly(projects.common.log)
//    compileOnly(projects.common.util)

//    implementation(platform(libs.compose.bom))
//    implementation(libs.bundles.compose)
//    implementation(libs.kotlin.reflect)
//    implementation(libs.coroutines.core)
//    implementation(libs.androidx.core)
//    implementation(libs.lifecycle.viewmodel)
//    implementation(libs.lifecycle.livedata)
}
