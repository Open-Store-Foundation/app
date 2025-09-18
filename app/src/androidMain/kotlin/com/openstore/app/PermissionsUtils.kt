package com.openstore.app

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
val PermissionState.shouldRequest: Boolean get() {
    return !status.isGranted && !status.shouldShowRationale
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberNotificationState(onResult: () -> Unit): PermissionState? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            permission = POST_NOTIFICATIONS,
            onPermissionResult = { onResult() }
        )
    } else {
        null
    }
}

val a = _root_ide_package_.androidx.compose.ui.graphics.Color(0xFF707A8A)