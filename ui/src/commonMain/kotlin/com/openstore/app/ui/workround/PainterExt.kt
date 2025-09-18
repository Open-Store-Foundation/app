package com.openstore.app.ui.workround

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter

fun Painter?.isEmpty(): Boolean {
    return this == null || intrinsicSize == Size.Unspecified
}
