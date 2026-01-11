import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        getByName("jvmMain") {
            dependencies {
                implementation(projects.lib.kitten.api)
                implementation(projects.lib.kitten.core)

                implementation(kotlin("reflect"))
                implementation(libs.kotlin.test)
                implementation("io.github.classgraph:classgraph:4.8.184")
            }
        }
    }
}
