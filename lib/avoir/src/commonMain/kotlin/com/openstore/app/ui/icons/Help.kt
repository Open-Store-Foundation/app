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

public val CommonIcons.Help: ImageVector
    get() {
        if (_help != null) {
            return _help!!
        }
        _help = Builder(name = "Help", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp,
                viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF707A8A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(10.0f, 17.5f)
                curveTo(14.1421f, 17.5f, 17.5f, 14.1421f, 17.5f, 10.0f)
                curveTo(17.5f, 5.8579f, 14.1421f, 2.5f, 10.0f, 2.5f)
                curveTo(5.8579f, 2.5f, 2.5f, 5.8579f, 2.5f, 10.0f)
                curveTo(2.5f, 14.1421f, 5.8579f, 17.5f, 10.0f, 17.5f)
                close()
                moveTo(9.9999f, 6.889f)
                curveTo(9.5091f, 6.889f, 9.1112f, 7.2869f, 9.1112f, 7.7777f)
                verticalLineTo(8.1332f)
                horizontalLineTo(7.3339f)
                verticalLineTo(7.7777f)
                curveTo(7.3339f, 6.3053f, 8.5275f, 5.1117f, 9.9999f, 5.1117f)
                curveTo(11.4723f, 5.1117f, 12.666f, 6.3053f, 12.666f, 7.7777f)
                curveTo(12.666f, 8.5137f, 12.3666f, 9.1814f, 11.8851f, 9.6629f)
                lineTo(10.8886f, 10.6594f)
                verticalLineTo(11.6879f)
                horizontalLineTo(9.1112f)
                verticalLineTo(9.9232f)
                lineTo(10.6283f, 8.4061f)
                curveTo(10.7901f, 8.2443f, 10.8886f, 8.0233f, 10.8886f, 7.7777f)
                curveTo(10.8886f, 7.2869f, 10.4907f, 6.889f, 9.9999f, 6.889f)
                close()
                moveTo(9.1112f, 14.8872f)
                verticalLineTo(13.1098f)
                horizontalLineTo(10.8886f)
                verticalLineTo(14.8872f)
                horizontalLineTo(9.1112f)
                close()
            }
        }
        .build()
        return _help!!
    }

private var _help: ImageVector? = null
