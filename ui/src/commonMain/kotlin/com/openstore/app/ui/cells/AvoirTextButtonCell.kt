package com.openstore.app.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AvoirTextButtonCell(
    title: String,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Text(
        text = title,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .height(48.dp)
            .padding(vertical = 12.dp, horizontal = 19.dp),
        style = MaterialTheme.typography.titleSmall,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}
