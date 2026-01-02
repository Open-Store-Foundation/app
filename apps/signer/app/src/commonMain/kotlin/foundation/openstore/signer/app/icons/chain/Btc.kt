package foundation.openstore.signer.app.icons.chain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ChainImage.Btc: ImageVector
    get() {
        if (_btc != null) {
            return _btc!!
        }
        _btc = Builder(name = "Btc", defaultWidth = 4091.3.dp, defaultHeight = 4091.7.dp,
                viewportWidth = 4091.3f, viewportHeight = 4091.7f).apply {
            path(fill = SolidColor(Color(0xFFF7931A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(4030.1f, 2540.8f)
                curveToRelative(-273.2f, 1096.0f, -1383.3f, 1763.0f, -2479.5f, 1489.7f)
                curveToRelative(-1095.7f, -273.2f, -1762.7f, -1383.4f, -1489.3f, -2479.3f)
                curveToRelative(273.1f, -1096.1f, 1383.2f, -1763.2f, 2479.0f, -1489.9f)
                curveToRelative(1096.1f, 273.2f, 1763.0f, 1383.5f, 1489.8f, 2479.6f)
                lineToRelative(0.0f, -0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(2947.8f, 1754.4f)
                curveToRelative(40.7f, -272.3f, -166.6f, -418.6f, -450.0f, -516.2f)
                lineToRelative(91.9f, -368.8f)
                lineToRelative(-224.5f, -55.9f)
                lineToRelative(-89.5f, 359.1f)
                curveToRelative(-59.0f, -14.7f, -119.6f, -28.6f, -179.9f, -42.3f)
                lineToRelative(90.2f, -361.5f)
                lineToRelative(-224.4f, -55.9f)
                lineToRelative(-92.0f, 368.7f)
                curveToRelative(-48.8f, -11.1f, -96.8f, -22.1f, -143.4f, -33.7f)
                lineToRelative(0.3f, -1.2f)
                lineToRelative(-309.6f, -77.3f)
                lineToRelative(-59.7f, 239.8f)
                curveToRelative(0.0f, 0.0f, 166.6f, 38.2f, 163.1f, 40.5f)
                curveToRelative(90.9f, 22.7f, 107.3f, 82.9f, 104.6f, 130.6f)
                lineToRelative(-104.7f, 420.1f)
                curveToRelative(6.3f, 1.6f, 14.4f, 3.9f, 23.3f, 7.5f)
                curveToRelative(-7.5f, -1.9f, -15.5f, -3.9f, -23.7f, -5.9f)
                lineToRelative(-146.8f, 588.6f)
                curveToRelative(-11.1f, 27.6f, -39.3f, 69.1f, -102.9f, 53.3f)
                curveToRelative(2.3f, 3.3f, -163.2f, -40.7f, -163.2f, -40.7f)
                lineToRelative(-111.5f, 257.0f)
                lineToRelative(292.1f, 72.8f)
                curveToRelative(54.3f, 13.6f, 107.6f, 27.9f, 160.1f, 41.3f)
                lineToRelative(-92.9f, 373.0f)
                lineToRelative(224.2f, 55.9f)
                lineToRelative(92.0f, -369.1f)
                curveToRelative(61.3f, 16.6f, 120.7f, 32.0f, 178.9f, 46.4f)
                lineToRelative(-91.7f, 367.3f)
                lineToRelative(224.5f, 55.9f)
                lineToRelative(92.9f, -372.3f)
                curveToRelative(382.8f, 72.4f, 670.7f, 43.2f, 791.8f, -303.0f)
                curveToRelative(97.6f, -278.8f, -4.9f, -439.6f, -206.3f, -544.4f)
                curveToRelative(146.7f, -33.8f, 257.2f, -130.3f, 286.6f, -329.6f)
                lineToRelative(-0.1f, -0.1f)
                close()
                moveTo(2434.8f, 2473.6f)
                curveToRelative(-69.4f, 278.8f, -538.8f, 128.1f, -690.9f, 90.3f)
                lineToRelative(123.3f, -494.2f)
                curveToRelative(152.2f, 38.0f, 640.2f, 113.2f, 567.7f, 403.9f)
                close()
                moveTo(2504.3f, 1750.3f)
                curveToRelative(-63.3f, 253.6f, -454.0f, 124.8f, -580.7f, 93.2f)
                lineToRelative(111.8f, -448.2f)
                curveToRelative(126.7f, 31.6f, 534.8f, 90.6f, 468.9f, 355.0f)
                lineToRelative(-0.0f, 0.0f)
                close()
            }
        }
        .build()
        return _btc!!
    }

private var _btc: ImageVector? = null

