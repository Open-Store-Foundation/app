package foundation.openstore.signer.app.icons.chain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ChainImage.Sol: ImageVector
    get() {
        if (_sol != null) {
            return _sol!!
        }
        _sol = Builder(name = "Sol", defaultWidth = 719.0.dp, defaultHeight = 719.0.dp,
                viewportWidth = 719.0f, viewportHeight = 719.0f).apply {
            group {
                path(fill = linearGradient(0.0f to Color(0xFF00FFA3), 1.0f to Color(0xFFDC1FFF),
                        start = Offset(520.88f,165.54f), end = Offset(301.21f,586.29f)), stroke =
                        null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(224.6f, 440.9f)
                    curveTo(227.0f, 438.5f, 230.3f, 437.1f, 233.8f, 437.1f)
                    horizontalLineTo(551.2f)
                    curveTo(557.0f, 437.1f, 559.9f, 444.1f, 555.8f, 448.2f)
                    lineTo(493.1f, 510.9f)
                    curveTo(490.7f, 513.3f, 487.4f, 514.7f, 483.9f, 514.7f)
                    horizontalLineTo(166.5f)
                    curveTo(160.7f, 514.7f, 157.8f, 507.7f, 161.9f, 503.6f)
                    lineTo(224.6f, 440.9f)
                    close()
                }
                path(fill = linearGradient(0.0f to Color(0xFF00FFA3), 1.0f to Color(0xFFDC1FFF),
                        start = Offset(424.83f,115.4f), end = Offset(205.16f,536.15f)), stroke =
                        null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(224.6f, 206.8f)
                    curveTo(227.1f, 204.4f, 230.4f, 203.0f, 233.8f, 203.0f)
                    horizontalLineTo(551.2f)
                    curveTo(557.0f, 203.0f, 559.9f, 210.0f, 555.8f, 214.1f)
                    lineTo(493.1f, 276.8f)
                    curveTo(490.7f, 279.2f, 487.4f, 280.6f, 483.9f, 280.6f)
                    horizontalLineTo(166.5f)
                    curveTo(160.7f, 280.6f, 157.8f, 273.6f, 161.9f, 269.5f)
                    lineTo(224.6f, 206.8f)
                    close()
                }
                path(fill = linearGradient(0.0f to Color(0xFF00FFA3), 1.0f to Color(0xFFDC1FFF),
                        start = Offset(472.55f,140.31f), end = Offset(252.88f,561.06f)), stroke =
                        null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(493.1f, 323.1f)
                    curveTo(490.7f, 320.7f, 487.4f, 319.3f, 483.9f, 319.3f)
                    horizontalLineTo(166.5f)
                    curveTo(160.7f, 319.3f, 157.8f, 326.3f, 161.9f, 330.4f)
                    lineTo(224.6f, 393.1f)
                    curveTo(227.0f, 395.5f, 230.3f, 396.9f, 233.8f, 396.9f)
                    horizontalLineTo(551.2f)
                    curveTo(557.0f, 396.9f, 559.9f, 389.9f, 555.8f, 385.8f)
                    lineTo(493.1f, 323.1f)
                    close()
                }
            }
        }
        .build()
        return _sol!!
    }

private var _sol: ImageVector? = null
