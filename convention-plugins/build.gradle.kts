import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(libs.kotlin.plugin)
    implementation(libs.android.plugin)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}

allprojects {
    tasks.withType<KotlinCompile> {
        compilerOptions {
//            allWarningsAsErrors.set(true)
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
