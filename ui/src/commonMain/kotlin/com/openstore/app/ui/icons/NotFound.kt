package com.openstore.app.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val CommonIcons.NotFound: ImageVector
    get() {
        if (notFound != null) {
            return notFound!!
        }
        notFound = Builder(name = "Not-found-data", defaultWidth = 96.0.dp, defaultHeight =
                96.0.dp, viewportWidth = 96.0f, viewportHeight = 96.0f).apply {
            path(fill = linearGradient(0.0f to Color(0x19929AA5), 1.0f to Color(0x3F929AA5), start =
                    Offset(55.0f,8.0f), end = Offset(55.0f,88.0f)), stroke = null, strokeLineWidth =
                    0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(64.0f, 8.0f)
                horizontalLineTo(26.0f)
                verticalLineTo(88.0f)
                horizontalLineTo(84.0f)
                verticalLineTo(28.0f)
                horizontalLineTo(64.0f)
                verticalLineTo(8.0f)
                close()
                moveTo(36.0f, 37.0f)
                horizontalLineTo(74.0f)
                verticalLineTo(41.0f)
                horizontalLineTo(36.0f)
                verticalLineTo(37.0f)
                close()
                moveTo(36.0f, 46.0f)
                horizontalLineTo(74.0f)
                verticalLineTo(50.0f)
                horizontalLineTo(36.0f)
                verticalLineTo(46.0f)
                close()
                moveTo(74.0f, 55.0f)
                horizontalLineTo(36.0f)
                verticalLineTo(59.0f)
                horizontalLineTo(74.0f)
                verticalLineTo(55.0f)
                close()
                moveTo(66.0f, 67.0f)
                lineTo(70.0f, 71.0f)
                lineTo(66.0f, 75.0f)
                lineTo(62.0f, 71.0f)
                lineTo(66.0f, 67.0f)
                close()
                moveTo(50.0f, 18.0f)
                lineTo(47.0f, 21.0f)
                lineTo(50.0f, 24.0f)
                lineTo(53.0f, 21.0f)
                lineTo(50.0f, 18.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF929AA5)), stroke = null, fillAlpha = 0.3f, strokeAlpha
                    = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(86.0f, 50.0f)
                lineTo(89.0f, 47.0f)
                lineTo(92.0f, 50.0f)
                lineTo(89.0f, 53.0f)
                lineTo(86.0f, 50.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF929AA5)), stroke = null, fillAlpha = 0.3f, strokeAlpha
                    = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(47.0f, 21.0f)
                lineTo(50.0f, 18.0f)
                lineTo(53.0f, 21.0f)
                lineTo(50.0f, 24.0f)
                lineTo(47.0f, 21.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF929AA5)), stroke = null, fillAlpha = 0.3f, strokeAlpha
                    = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(84.0f, 28.0f)
                horizontalLineTo(64.0f)
                verticalLineTo(8.0f)
                lineTo(84.0f, 28.0f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFF929AA5), 1.0f to Color(0xFF76808F), start =
                    Offset(4.17155f,68.75f), end = Offset(24.3284f,68.75f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(4.1715f, 73.1716f)
                lineTo(18.6716f, 58.6716f)
                lineTo(24.3284f, 64.3285f)
                lineTo(9.8284f, 78.8284f)
                lineTo(4.1715f, 73.1716f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFF929AA5), 1.0f to Color(0xFF76808F), start =
                    Offset(15.0f,48.0f), end = Offset(55.0f,48.0f)), stroke = null, strokeLineWidth
                    = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(51.0f, 48.0f)
                curveTo(51.0f, 39.1634f, 43.8366f, 32.0f, 35.0f, 32.0f)
                curveTo(26.1634f, 32.0f, 19.0f, 39.1634f, 19.0f, 48.0f)
                curveTo(19.0f, 56.8366f, 26.1634f, 64.0f, 35.0f, 64.0f)
                curveTo(43.8366f, 64.0f, 51.0f, 56.8366f, 51.0f, 48.0f)
                close()
                moveTo(55.0f, 48.0f)
                curveTo(55.0f, 36.9543f, 46.0457f, 28.0f, 35.0f, 28.0f)
                curveTo(23.9543f, 28.0f, 15.0f, 36.9543f, 15.0f, 48.0f)
                curveTo(15.0f, 59.0457f, 23.9543f, 68.0f, 35.0f, 68.0f)
                curveTo(46.0457f, 68.0f, 55.0f, 59.0457f, 55.0f, 48.0f)
                close()
            }
        }
        .build()
        return notFound!!
    }

private var notFound: ImageVector? = null
