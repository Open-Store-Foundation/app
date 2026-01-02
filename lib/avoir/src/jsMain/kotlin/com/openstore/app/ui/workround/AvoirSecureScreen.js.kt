package com.openstore.app.ui.workround

import androidx.compose.runtime.Composable

@Composable
actual fun AvoidSecureScreen(content: @Composable (() -> Unit)) {
    content()
}