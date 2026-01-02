package com.openstore.app.ui.workround

import androidx.compose.runtime.Composable

@Composable
expect fun AvoidSecureScreen(
    content: @Composable () -> Unit
)