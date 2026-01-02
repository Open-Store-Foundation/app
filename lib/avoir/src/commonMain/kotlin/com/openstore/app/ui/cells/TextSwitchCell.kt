package com.openstore.app.ui.cells

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.openstore.app.ui.component.AvoirSwitch

@Composable
fun TextSwitchCell(
    title: CharSequence,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    image: @Composable (() -> Unit)? = null,
    subtitle: CharSequence? = null,
    enabled: Boolean = true,
) {
    TextCell(
        title = title,
        modifier = modifier,
        image = image,
        subtitle = subtitle,
        content = {
            AvoirSwitch(
                checked = isChecked,
                onCheckedChange = onClick?.let {
                    { onCheckedChange?.invoke(!isChecked) }
                },
                enabled = enabled,
            )
        },
        onClick = {
            if (onClick != null) {
                onClick()
            } else {
                onCheckedChange?.invoke(!isChecked)
            }
        }
    )
}

@Composable
fun TextSwitchCell(
    title: (@Composable () -> Unit),
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    image: @Composable (() -> Unit)? = null,
    subtitle: CharSequence? = null,
    enabled: Boolean = true,
) {
    TextCell(
        title = title,
        modifier = modifier,
        image = image,
        subtitle = subtitle,
        content = {
            AvoirSwitch(
                checked = isChecked,
                onCheckedChange = onClick?.let {
                    { onCheckedChange?.invoke(!isChecked) }
                },
                enabled = enabled,
            )
        },
        onClick = {
            if (onClick != null) {
                onClick()
            } else {
                onCheckedChange?.invoke(!isChecked)
            }
        }
    )
}
