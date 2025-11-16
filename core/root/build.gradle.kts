plugins {
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation("com.scottyab:rootbeer-lib:0.1.1")
            }
        }
    }
}
