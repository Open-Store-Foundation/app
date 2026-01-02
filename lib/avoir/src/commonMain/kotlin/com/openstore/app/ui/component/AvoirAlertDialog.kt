package com.openstore.app.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale

@Composable
fun AvoirAlertDialog(
    title: String,
    text: String,
    confirmText: String,
    cancelText: String? = null,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    isDangerous: Boolean = false,
) {
    AlertDialog(
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = {
                    if (isDangerous) {
                        Text(confirmText, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text(confirmText)
                    }
                }
            )
        },
        dismissButton = {
            if (cancelText != null && onCancel != null) {
                TextButton(
                    onClick = onCancel,
                    content = {
                        Text(cancelText, color = MaterialTheme.colorScheme.onSurface)
                    }
                )
            }
        },
        onDismissRequest = onDismiss,
    )
}
