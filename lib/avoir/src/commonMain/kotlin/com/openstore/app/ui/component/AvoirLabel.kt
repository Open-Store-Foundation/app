package com.openstore.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onTertiary,
    badgeColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    Text(
        modifier = modifier
            .clip(CircleShape)
            .background(badgeColor)
            .padding(vertical = 2.dp, horizontal = 8.dp),
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = textColor,
        maxLines = 1
    )
}

@Composable
fun SquareLabel(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(vertical = 2.dp, horizontal = 4.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onTertiary,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}


@Composable
fun BigSquareLabel(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onTertiary,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium
        )
    }
}