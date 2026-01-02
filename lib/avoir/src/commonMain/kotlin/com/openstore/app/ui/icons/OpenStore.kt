package com.openstore.app.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val CommonIcons.OpenStore: ImageVector
    get() {
        if (`_favicon-color` != null) {
            return `_favicon-color`!!
        }
        `_favicon-color` = Builder(name = "Favicon-color", defaultWidth = 94.0.dp, defaultHeight =
                117.0.dp, viewportWidth = 94.0f, viewportHeight = 117.0f).apply {
            path(fill = linearGradient(0.0f to Color(0xFF467DEE), 1.0f to Color(0xFFE6434F), start =
                    Offset(47.0f,0.0f), end = Offset(47.0f,117.0f)), stroke = null, strokeLineWidth
                    = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(23.3f, 0.0f)
                curveTo(36.74f, 15.05f, 45.21f, 29.15f, 66.9f, 33.64f)
                curveTo(74.96f, 28.8f, 78.59f, 29.04f, 90.58f, 30.47f)
                curveTo(93.21f, 30.99f, 93.0f, 31.0f, 94.0f, 32.0f)
                curveTo(91.13f, 33.36f, 91.08f, 34.52f, 90.98f, 37.68f)
                curveTo(91.6f, 68.47f, 76.19f, 77.7f, 35.79f, 101.88f)
                curveTo(28.34f, 106.33f, 20.05f, 111.3f, 10.85f, 117.0f)
                curveTo(11.07f, 103.36f, 14.87f, 95.42f, 18.08f, 88.73f)
                curveTo(18.94f, 86.94f, 19.75f, 85.24f, 20.44f, 83.55f)
                curveTo(14.94f, 79.68f, 10.93f, 75.08f, 8.37f, 68.21f)
                curveTo(13.31f, 68.69f, 17.59f, 70.58f, 21.69f, 69.51f)
                curveTo(6.56f, 65.19f, 2.43f, 54.96f, 0.0f, 39.97f)
                curveTo(4.2f, 42.34f, 6.92f, 45.86f, 12.38f, 46.11f)
                curveTo(0.39f, 35.41f, -0.19f, 17.49f, 6.72f, 0.25f)
                curveTo(11.3f, 5.38f, 15.36f, 10.41f, 19.48f, 15.06f)
                curveTo(19.97f, 10.14f, 21.29f, 5.04f, 23.3f, 0.0f)
                close()
            }
        }
        .build()
        return `_favicon-color`!!
    }

private var `_favicon-color`: ImageVector? = null
