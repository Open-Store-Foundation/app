plugins {
    id("app.android")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    android {
        namespace = "com.openwallet.example.app"

        defaultConfig {
            applicationId = "foundation.openwallet.example"
            versionCode = 1
            versionName = "1.0"
        }
    }

    dependencies {
        implementation(libs.jetbrains.compose.runtime)
        implementation(libs.jetbrains.compose.ui.tooling.preview)
        implementation(libs.android.lifecycle.process)
        implementation(libs.android.appcompat)
        implementation(libs.android.permissions)
        implementation(libs.android.compose.activity)

        implementation(projects.apps.sample.wallet.shared)
        implementation(projects.core.log)
    }
}
