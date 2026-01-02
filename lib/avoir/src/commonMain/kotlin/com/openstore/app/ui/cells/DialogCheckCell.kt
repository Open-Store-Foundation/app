package com.openstore.app.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.DefaultItemTitle

@Composable
fun DialogCheckCell(
    title: String,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Row(
        Modifier.fillMaxWidth()
            .defaultMinSize(minHeight = 55.dp)
            .clickable(
                onClick = { onCheckedChange?.invoke(!isChecked) },
//                indication = null,
//                interactionSource = null
            )
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DefaultItemTitle(title)

        if (isChecked) {
            Spacer(modifier = Modifier.weight(1f))

            DefaultItemIcon(
                vector = Icons.Default.Check,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
