plugins {
    id("core.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.log)
            implementation(projects.core.common)
        }
    }
}
