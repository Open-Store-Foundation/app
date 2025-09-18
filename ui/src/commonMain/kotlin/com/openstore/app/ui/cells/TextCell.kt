package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultItemSubtitle
import com.openstore.app.ui.component.DefaultItemTitle

@Composable
fun TextCell(
    modifier: Modifier = Modifier,
    title: CharSequence,
    subtitle: CharSequence? = null,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    image: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    isContentHasPriority: Boolean = true,
    maxLinesTitle: Int = 1,
    maxLinesSubtitle: Int = 1,
    onClick: (() -> Unit)? = null,
    minHeight: Dp = DefaultItemHeight,
) {
    val newModifier = if (modifier === Modifier) {
        modifier.padding(horizontal = 19.dp)
    } else {
        modifier
    }

    BaseTextCell(
        modifier = newModifier,
        isContentHasPriority = isContentHasPriority,
        icon = image?.let {
            { image() }
        },
        title = {
            DefaultItemTitle(
                text = title,
                maxLines = maxLinesTitle,
            )
        },
        subtitle = if (subtitle.isNullOrBlank()) {
            null
        } else {
            {
                DefaultItemSubtitle(
                    text = subtitle,
                    color = subtitleColor,
                    maxLines = maxLinesSubtitle,
                )
            }
        },
        content = content,
        onClick = onClick,
        minHeight = minHeight,
    )
}

@Composable
fun TextCell(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit),
    subtitle: CharSequence? = null,
    image: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    maxLinesSubtitle: Int = 2,
    onClick: (() -> Unit)? = null,
    minHeight: Dp = DefaultItemHeight,
) {
    val newModifier = if (modifier === Modifier) {
        modifier.padding(horizontal = 19.dp)
    } else {
        modifier
    }

    BaseTextCell(
        modifier = newModifier,
        icon = image?.let {
            { image() }
        },
        title = {
            title()
        },
        subtitle = if (subtitle.isNullOrBlank()) {
            null
        } else {
            { DefaultItemSubtitle(text = subtitle, maxLines = maxLinesSubtitle) }
        },
        content = content,
        onClick = onClick,
        minHeight = minHeight,
    )
}
