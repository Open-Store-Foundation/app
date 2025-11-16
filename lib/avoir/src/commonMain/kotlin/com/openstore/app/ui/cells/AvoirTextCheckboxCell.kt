package com.openstore.app.ui.cells

import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AvoirTextCheckboxCell(
    title: String,
    subtitle: String? = null,
    subtitleColor: Color = MaterialTheme.colorScheme.onBackground,
    isChecked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    maxLinesSubtitle: Int = 1,
    image: @Composable (() -> Unit)? = null,
) {
    TextCell(
        title = title,
        modifier = modifier,
        image = image,
        subtitle = subtitle,
        maxLinesSubtitle = maxLinesSubtitle,
        subtitleColor = subtitleColor,
        content = {
            Checkbox(isChecked, onCheckedChange = null, enabled = enabled)
        },
        onClick = if (enabled) {
            { onCheckedChange?.invoke(!isChecked) }
        } else {
            null
        },
    )
}
