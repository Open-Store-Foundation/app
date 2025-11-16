package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AvoirDescriptionCell(
    text: String?,
    modifier: Modifier = Modifier,
    cellPaddings: Boolean = true,
    textAlign: TextAlign = TextAlign.Start
) {
    if (text != null) {
        Text(
            modifier = modifier.fillMaxWidth()
                .padding(
                    bottom = 20.dp,
                    start = if (cellPaddings) 19.dp else 0.dp,
                    end = if (cellPaddings) 19.dp else 0.dp,
                ),
            text = text,
            textAlign = textAlign,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 5,
        )
    }
}
