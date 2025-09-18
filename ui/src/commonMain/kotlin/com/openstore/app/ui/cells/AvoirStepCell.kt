package com.openstore.app.ui.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AvoirStepCell(
    isActive: Boolean
) {
    Spacer(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .size(width = 72.dp, height = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = if (isActive) 1f else 0.1f)),
    )
}
