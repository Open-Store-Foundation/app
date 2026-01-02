package com.openstore.app.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AvoirBundleOutlined(
    modifier: Modifier = Modifier
        .padding(horizontal = 19.dp, vertical = 8.dp),
    onClick: (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.outline,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = color,
                shape = MaterialTheme.shapes.medium,
            )
            .run {
                if (onClick != null) {
                    clickable {
                        onClick()
                    }
                } else {
                    this
                }
            },
    ) {
        content()
    }
}
