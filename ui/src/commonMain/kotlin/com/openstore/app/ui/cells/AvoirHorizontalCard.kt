package com.openstore.app.ui.cells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.cells.BaseTextCell
import com.openstore.app.ui.cells.TextValueCell
import com.openstore.app.ui.component.DefaultItemTitle
import com.openstore.app.ui.component.DefaultTinyItemTitle
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.animations.AnimatedContainer
import com.openstore.app.ui.component.SquareLabel

@Composable
fun AvoirTitledCardCell(
    title: String,
    icon: @Composable () -> Unit,
    labels: (@Composable () -> Unit)? = null,
    labelsBottom: (@Composable () -> Unit)? = null,
    trailingComponent: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    tag: String? = null,
    modifier: Modifier = Modifier,
    wrapPadding: Boolean = false,
    onClick: () -> Unit = {},
    defaultMinSize: Dp = 78.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    AvoirBundle(
        modifier = modifier
            .defaultMinSize(minHeight = defaultMinSize)
            .padding(horizontal = if (wrapPadding) 0.dp else 19.dp),
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 19.dp),
        ) {
            AvoirTitledCardTextCell(title, icon, labels, labelsBottom, trailingComponent, subtitle, tag)
        }
    }
}

@Composable
fun AvoirTitledCardTextCell(
    title: String?,
    image: (@Composable () -> Unit)? = null,
    labels: (@Composable () -> Unit)? = null,
    labelsBottom: (@Composable () -> Unit)? = null,
    trailingComponent: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    tag: String? = null,
    defaultMinSize: Dp = 78.dp,
) {
    BaseTextCell(
        modifier = Modifier
            .fillMaxWidth(),
        minHeight = defaultMinSize,
        paddingBetween = true,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (title != null) {
                    DefaultTinyItemTitle(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (labels != null) {
                    labels()
                }
            }
        },
        icon = image,
        subtitle = subtitle?.let {
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DefaultItemTitle(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    if (labelsBottom != null) {
                        labelsBottom()
                    }
                }
            }
        },
        content = {
            if (tag != null) {
                Text(
                    text = tag,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }


            if (trailingComponent != null) {
                trailingComponent()
            }
        }
    )
}

@Composable
fun AvoirCardCell(
    title: String,
    image: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    labels: (@Composable () -> Unit)? = null,
    isExpanded: Boolean = false,
    trailingComponent: (@Composable () -> Unit)? = null,
    expandableContent: (@Composable () -> Unit)? = null,
    subtitle: CharSequence? = null,
    tag: String? = null,
    wrapPadding: Boolean = false,
    onClick: () -> Unit = {},
    defaultMinSize: Dp = 78.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    AvoirBundle(
        modifier = modifier
            .defaultMinSize(minHeight = defaultMinSize)
            .padding(horizontal = if (wrapPadding) 0.dp else 19.dp),
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 19.dp),
        ) {
            AvoirCardTextCell(title, image, labels, trailingComponent, subtitle, tag, defaultMinSize)

            AnimatedContainer(isExpanded = expandableContent != null && isExpanded) {
                Column {
                    expandableContent?.invoke()
                }
            }
        }
    }
}

@Composable
fun AvoirCardTextCell(
    title: CharSequence,
    image: (@Composable () -> Unit)? = null,
    labels: (@Composable () -> Unit)? = null,
    trailingComponent: (@Composable () -> Unit)? = null,
    subtitle: CharSequence? = null,
    tag: String? = null,
    defaultMinSize: Dp = 78.dp,
) {
    TextValueCell(
        modifier = Modifier.fillMaxWidth(),
        paddingBetween = false,
        defaultMinSize = defaultMinSize,
        title = title,
        subtitle = subtitle,
        labels = labels,
        image = image,
        content = {
            if (tag != null) {
                Text(
                    text = tag,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                )
            }


            if (trailingComponent != null) {
                trailingComponent()
            }
        },
        clip = RectangleShape
    )
}

@Composable
fun AvoirCardCell(
    onClick: () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    iconNext: @Composable (() -> Unit)? = null,
    icon: @Composable (modifier: Modifier) -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    selection: (@Composable () -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
) {
    AvoirBundle(
        modifier = Modifier
            .defaultMinSize(minHeight = 80.dp)
            .border(border)
            .then(modifier),
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon(
                    Modifier
                        .padding(horizontal = 19.dp, vertical = 20.dp)
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.extraSmall),
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    title()
                    if (subtitle != null) {
                        subtitle()
                    }
                }

                if (iconNext != null) {
                    iconNext()
                }
            }

            if (selection != null) {
                Spacer(modifier = Modifier.weight(1f))
                selection()
            }
        }
    }
}

