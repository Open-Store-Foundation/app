package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultItemSubtitle
import com.openstore.app.ui.component.DefaultItemTitle
import com.openstore.app.ui.component.DefaultSmallItemTitle

@Composable
fun TextValueCell(
    modifier: Modifier = Modifier,
    title: CharSequence,
    image: @Composable (() -> Unit)? = null,
    labels: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    subtitle: CharSequence? = null,
    value: CharSequence? = null,
    valueDescription: CharSequence? = null,
    paddingBetween: Boolean = false,
    descriptionMaxLines: Int = 1,
    isContentHasPriority: Boolean = false,
    clip: Shape = RectangleShape,
    titleInline: Map<String, InlineTextContent> = emptyMap(),
    defaultMinSize: Dp = 58.dp,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val newModifier = if (modifier === Modifier) {
        modifier.padding(horizontal = 19.dp)
    } else {
        modifier
    }

    BaseTextCell(
        modifier = newModifier,
        minHeight = defaultMinSize,
        paddingBetween = paddingBetween,
        isContentHasPriority = isContentHasPriority,
        icon = image,
        header = header,
        title = {
            if (labels != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    DefaultItemTitle(
                        text = title,
                        modifier = Modifier.weight(1f, fill = false),
                        inlineContent = titleInline
                    )

                    labels()
                }
            } else {
                DefaultItemTitle(title, inlineContent = titleInline)
            }
        },
        subtitle = if (subtitle.isNullOrBlank()) {
            null
        } else {
            {
                DefaultItemSubtitle(
                    text = subtitle,
                    maxLines = descriptionMaxLines,
                )
            }
        },
        content = value?.let {
            {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (!valueDescription.isNullOrBlank()) {
                        DefaultItemTitle(text = value)
                        Spacer(modifier = Modifier.height(2.dp))
                        DefaultItemSubtitle(valueDescription, maxLines = descriptionMaxLines)
                    } else {
                        DefaultSmallItemTitle(text = value, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        } ?: content,
        clip = clip,
        onLongClick = onLongClick,
        onClick = onClick,
    )
}
