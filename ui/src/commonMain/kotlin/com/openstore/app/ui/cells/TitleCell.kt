package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.SquareLabel

@Composable
fun TitleCell(
    text: String,
    modifier: Modifier = Modifier,
    wrapPadding: Boolean = false,
    label: String? = null,
    isBundle: Boolean = false,
    onSeeAll: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (wrapPadding) 0.dp else 19.dp,
                top = 20.dp,
                end = if (wrapPadding) 0.dp else 19.dp,
                bottom = if (isBundle) 14.dp else 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.outline,
        )

        if (label != null) {
            Spacer(modifier = Modifier.width(6.dp))
            SquareLabel(title = label)
        }

        if (onSeeAll != null) {
            Spacer(modifier = Modifier.weight(1f))

            DefaultItemIcon(
                Icons.AutoMirrored.Filled.ArrowForward,
                color = MaterialTheme.colorScheme.primary,
                size = 26.dp
            ) {
                onSeeAll()
            }
        }
    }
}

@Composable
fun SmallTitleCell(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 19.dp, top = 15.dp, end = 19.dp, bottom = 8.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline,
    )
}

