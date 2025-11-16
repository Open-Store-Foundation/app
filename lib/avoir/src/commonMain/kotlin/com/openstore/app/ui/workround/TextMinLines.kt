package com.openstore.app.ui.workround

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle

fun Modifier.textLines(typography: TextStyle, count: Int) = composed {
    return@composed sizeIn(
        minHeight = with(LocalDensity.current) {
            (typography.lineHeight).toDp() * count
        }
    )
}
