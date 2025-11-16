package com.openstore.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import org.jetbrains.compose.reload.DevelopmentEntryPoint

fun main() {
    singleWindowApplication(
        title = "My CHR App",
        state = WindowState(width = 393.dp, height = 851.dp),
        alwaysOnTop = true
    ) {
        DevelopmentEntryPoint {
            App()
        }
    }
}
