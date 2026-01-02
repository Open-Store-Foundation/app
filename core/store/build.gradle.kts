plugins {
    id("core.multiplatform")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(libs.settings)
            }
        }

        getByName("androidMain") {
            dependencies {
                implementation(libs.settings)
            }
        }

//        iosMain {
//            dependencies {
//                implementation(libs.settings)
//            }
//        }

        getByName("jsMain") {
            dependencies {
                implementation(projects.core.os)
                implementation(libs.settings)
            }
        }
    }
}
