package com.openstore.app.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AvoirSwitch(
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)?,
    colors: SwitchColors = switchDefaultsColors()
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = colors,
        enabled = enabled,
    )
}

@Composable
private fun switchDefaultsColors(): SwitchColors {
    return SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        checkedBorderColor = Color.Transparent,
        checkedIconColor =  Color.Transparent,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = MaterialTheme.colorScheme.outline,
        uncheckedBorderColor = Color.Transparent,
        uncheckedIconColor = Color.Transparent,
        disabledCheckedThumbColor = Color.White.copy(alpha = 0.38f),
        disabledCheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        disabledCheckedBorderColor = Color.Transparent,
        disabledCheckedIconColor = Color.Transparent,
        disabledUncheckedThumbColor = Color.White,
        disabledUncheckedTrackColor = MaterialTheme.colorScheme.primary,
        disabledUncheckedBorderColor = Color.Transparent,
        disabledUncheckedIconColor = Color.Transparent,
    )
}
