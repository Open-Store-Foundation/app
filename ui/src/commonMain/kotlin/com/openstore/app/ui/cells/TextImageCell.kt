package com.openstore.app.ui.cells

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.openstore.app.ui.component.DefaultItemIcon

@Composable
fun TextIconCell(
    title: String,
    image: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    subtitle: String? = null,
    subtitleColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (() -> Unit)? = null,
) {
    TextCell(
        title = title,
        modifier = modifier,
        image = image,
        subtitle = subtitle,
        subtitleColor = subtitleColor,
        content = trailingIcon,
        onClick = onClick,
    )
}

@Composable
fun TextIconCell(
    title: String,
    image: ImageVector?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    subtitleColor: Color = MaterialTheme.colorScheme.onBackground,
    trailingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
) {
    TextCell(
        title = title,
        modifier = modifier,
        image = image?.let { { DefaultItemIcon(vector = it) } },
        subtitle = subtitle,
        subtitleColor = subtitleColor,
        content = trailingIcon?.let { { DefaultItemIcon(vector = it) } },
        onClick = onClick,
    )
}
