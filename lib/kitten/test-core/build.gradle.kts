plugins {
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.lib.kitten.api)
                implementation(projects.lib.kitten.core)
            }
        }
    }
}
