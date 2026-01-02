package com.openstore.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val CommonIcons.DarkMode: ImageVector
    get() {
        if (_darkmode != null) {
            return _darkmode!!
        }
        _darkmode = Builder(name = "Darkmode", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp,
                viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF707A8A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(17.4731f, 10.6402f)
                curveTo(16.5334f, 11.2879f, 15.3943f, 11.6671f, 14.1667f, 11.6671f)
                curveTo(10.945f, 11.6671f, 8.3333f, 9.0554f, 8.3333f, 5.8338f)
                curveTo(8.3333f, 4.6061f, 8.7126f, 3.4671f, 9.3603f, 2.5273f)
                curveTo(5.5176f, 2.8519f, 2.5f, 6.0738f, 2.5f, 10.0004f)
                curveTo(2.5f, 14.1426f, 5.8579f, 17.5004f, 10.0f, 17.5004f)
                curveTo(13.9266f, 17.5004f, 17.1485f, 14.4829f, 17.4731f, 10.6402f)
                close()
            }
        }
        .build()
        return _darkmode!!
    }

private var _darkmode: ImageVector? = null
