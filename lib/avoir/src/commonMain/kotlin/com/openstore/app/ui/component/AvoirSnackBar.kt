package com.openstore.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun AvoirSnackBar(
    imageVector: ImageVector,
    message: String,
    isRtl: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    containerColor: Color = MaterialTheme.colorScheme.secondary
) {
    Snackbar(
        containerColor = containerColor
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides
                if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                DefaultItemImage(
                    image = imageVector,
                    size = 16.dp,
                )
                Text(
                    text = message,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
