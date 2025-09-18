plugins {
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.kitten.api)
                implementation("dev.opensavvy.pedestal:weak:3.0.0-alpha.2")
            }
        }
    }
}
