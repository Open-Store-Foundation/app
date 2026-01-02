package com.openstore.app.ui.workround

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.openstore.app.ui.lifecycle.OnPause
import com.openstore.app.ui.lifecycle.OnResume

@Composable
actual fun AvoidSecureScreen(
    content: @Composable () -> Unit
) {
    val window = LocalContext.current.requireActivity().window

    OnResume {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    OnPause {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    content()
}
