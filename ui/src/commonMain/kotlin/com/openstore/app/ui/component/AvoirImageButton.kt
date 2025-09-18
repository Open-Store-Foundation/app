package com.openstore.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AvoirImageButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
       modifier = Modifier
           .clip(MaterialTheme.shapes.extraSmall)
           .background(MaterialTheme.colorScheme.secondaryContainer)
           .size(30.dp)
           .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
