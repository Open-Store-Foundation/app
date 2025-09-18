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
fun AvoirDescriptionCell(text: String?, textAlign: TextAlign = TextAlign.Start) {
    if (text != null) {
        Text(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 20.dp, start = 19.dp, end = 19.dp),
            text = text,
            textAlign = textAlign,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 5,
        )
    }
}
