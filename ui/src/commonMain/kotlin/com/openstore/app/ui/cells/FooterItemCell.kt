package com.openstore.app.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.DefaultItemTitle

@Composable
fun FooterItemCell(
    title: String,
    vector: ImageVector,
    onClick: () -> Unit
) {
    BaseTextCell(
        modifier = Modifier.padding(horizontal = 19.dp),
        title = {
            DefaultItemTitle(
                text = title,
                color = MaterialTheme.colorScheme.primary
            )
        } ,
        icon = {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                DefaultItemIcon(
                    vector = vector,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        onClick = onClick
    )
}

@Composable
fun AvoirFooterItemCell(title: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(15.dp),
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.tertiary,
        textAlign = TextAlign.Center,
    )
}
