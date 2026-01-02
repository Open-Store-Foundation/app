package com.openstore.app.ui.cells

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.openstore.app.ui.component.DefaultItemIcon

@Composable
fun TextCheckCell(
    title: String,
    subtitle: String? = null,
    subtitleColor: Color = MaterialTheme.colorScheme.onBackground,
    isChecked: Boolean,
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
            if (isChecked) {
                DefaultItemIcon(
                    vector = Icons.Default.Check,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        onClick = {
            onCheckedChange?.invoke(!isChecked)
        },
    )
}
