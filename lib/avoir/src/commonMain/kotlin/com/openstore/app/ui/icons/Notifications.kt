package com.openstore.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val CommonIcons.Notifications: ImageVector
    get() {
        if (`_notifications-f` != null) {
            return `_notifications-f`!!
        }
        `_notifications-f` = Builder(name = "Notifications-f", defaultWidth = 20.0.dp, defaultHeight
                = 20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF707A8A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(9.9999f, 2.5f)
                curveTo(6.7783f, 2.5f, 4.1666f, 5.1117f, 4.1666f, 8.3333f)
                verticalLineTo(10.8333f)
                lineTo(3.3333f, 11.6667f)
                verticalLineTo(13.3333f)
                horizontalLineTo(4.1666f)
                horizontalLineTo(15.8333f)
                horizontalLineTo(16.6666f)
                verticalLineTo(11.6667f)
                lineTo(15.8333f, 10.8333f)
                verticalLineTo(8.3333f)
                curveTo(15.8333f, 5.1117f, 13.2216f, 2.5f, 9.9999f, 2.5f)
                close()
                moveTo(6.1799f, 15.0f)
                horizontalLineTo(13.8199f)
                curveTo(13.1769f, 16.4716f, 11.7085f, 17.5f, 9.9999f, 17.5f)
                curveTo(8.2913f, 17.5f, 6.8229f, 16.4716f, 6.1799f, 15.0f)
                close()
            }
        }
        .build()
        return `_notifications-f`!!
    }

private var `_notifications-f`: ImageVector? = null
