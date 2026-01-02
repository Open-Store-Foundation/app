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

public val CommonIcons.X: ImageVector
    get() {
        if (_x != null) {
            return _x!!
        }
        _x = Builder(name = "X", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp, viewportWidth =
                20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF707A8A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(18.3366f, 10.0033f)
                curveTo(18.3366f, 14.6056f, 14.6056f, 18.3366f, 10.0033f, 18.3366f)
                curveTo(5.4009f, 18.3366f, 1.6699f, 14.6056f, 1.6699f, 10.0033f)
                curveTo(1.6699f, 5.4009f, 5.4009f, 1.6699f, 10.0033f, 1.6699f)
                curveTo(14.6056f, 1.6699f, 18.3366f, 5.4009f, 18.3366f, 10.0033f)
                close()
                moveTo(8.0504f, 5.4004f)
                horizontalLineTo(4.8249f)
                lineTo(8.7556f, 10.656f)
                lineTo(4.7715f, 14.9601f)
                horizontalLineTo(5.9184f)
                lineTo(9.2682f, 11.3414f)
                lineTo(11.9745f, 14.9601f)
                horizontalLineTo(15.2f)
                lineTo(11.0521f, 9.4141f)
                lineTo(14.7675f, 5.4004f)
                horizontalLineTo(13.6205f)
                lineTo(10.5397f, 8.7287f)
                lineTo(8.0504f, 5.4004f)
                close()
                moveTo(12.3972f, 14.1159f)
                lineTo(6.5102f, 6.2445f)
                horizontalLineTo(7.6275f)
                lineTo(13.5145f, 14.1159f)
                horizontalLineTo(12.3972f)
                close()
            }
        }
        .build()
        return _x!!
    }

private var _x: ImageVector? = null
