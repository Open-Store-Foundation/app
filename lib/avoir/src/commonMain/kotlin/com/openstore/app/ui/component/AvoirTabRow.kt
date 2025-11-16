package com.openstore.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AvoirTabRow(
    isSelected: Boolean,
    title: String,
    onClick: () -> Unit
) {
    Tab(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(
                when (isSelected) {
                    true -> MaterialTheme.colorScheme.surface
                    false -> MaterialTheme.colorScheme.background
                }
            ),
        text = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
        selected = isSelected,
        onClick = onClick,
    )
}
