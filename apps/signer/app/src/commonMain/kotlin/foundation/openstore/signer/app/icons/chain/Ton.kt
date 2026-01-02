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

public val ChainImage.Ton: ImageVector
    get() {
        if (_ton != null) {
            return _ton!!
        }
        _ton = Builder(name = "Ton", defaultWidth = 56.0.dp, defaultHeight = 56.0.dp, viewportWidth
                = 56.0f, viewportHeight = 56.0f).apply {
            path(fill = SolidColor(Color(0xFF0098EA)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(28.0f, 56.0f)
                curveToRelative(15.5f, 0.0f, 28.0f, -12.5f, 28.0f, -28.0f)
                reflectiveCurveTo(43.5f, 0.0f, 28.0f, 0.0f)
                reflectiveCurveTo(0.0f, 12.5f, 0.0f, 28.0f)
                reflectiveCurveTo(12.5f, 56.0f, 28.0f, 56.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(37.6f, 15.6f)
                horizontalLineTo(18.4f)
                curveToRelative(-3.5f, 0.0f, -5.7f, 3.8f, -4.0f, 6.9f)
                lineToRelative(11.8f, 20.5f)
                curveToRelative(0.8f, 1.3f, 2.7f, 1.3f, 3.5f, 0.0f)
                lineToRelative(11.8f, -20.5f)
                curveTo(43.3f, 19.4f, 41.1f, 15.6f, 37.6f, 15.6f)
                lineTo(37.6f, 15.6f)
                close()
                moveTo(26.3f, 36.8f)
                lineToRelative(-2.6f, -5.0f)
                lineToRelative(-6.2f, -11.1f)
                curveToRelative(-0.4f, -0.7f, 0.1f, -1.6f, 1.0f, -1.6f)
                horizontalLineToRelative(7.8f)
                lineTo(26.3f, 36.8f)
                lineTo(26.3f, 36.8f)
                close()
                moveTo(38.5f, 20.7f)
                lineToRelative(-6.2f, 11.1f)
                lineToRelative(-2.6f, 5.0f)
                verticalLineTo(19.1f)
                horizontalLineToRelative(7.8f)
                curveTo(38.4f, 19.1f, 38.9f, 20.0f, 38.5f, 20.7f)
                close()
            }
        }
        .build()
        return _ton!!
    }

private var _ton: ImageVector? = null
