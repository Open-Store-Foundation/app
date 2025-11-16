package foundation.openstore.signer.app.icons.chain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ChainImage.Tron: ImageVector
    get() {
        if (_tron != null) {
            return _tron!!
        }
        _tron = Builder(name = "Tron", defaultWidth = 110.0.dp, defaultHeight = 110.0.dp,
                viewportWidth = 110.0f, viewportHeight = 110.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFFF060A)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(86.55f, 44.28f)
                    curveTo(83.55f, 41.51f, 79.4f, 37.28f, 76.02f, 34.28f)
                    lineTo(75.82f, 34.14f)
                    curveTo(75.49f, 33.87f, 75.11f, 33.66f, 74.71f, 33.52f)
                    curveTo(66.56f, 32.0f, 28.63f, 24.91f, 27.89f, 25.0f)
                    curveTo(27.68f, 25.03f, 27.48f, 25.1f, 27.31f, 25.22f)
                    lineTo(27.12f, 25.37f)
                    curveTo(26.89f, 25.61f, 26.71f, 25.89f, 26.6f, 26.21f)
                    lineTo(26.55f, 26.34f)
                    verticalLineTo(27.05f)
                    verticalLineTo(27.16f)
                    curveTo(30.82f, 39.05f, 47.68f, 78.0f, 51.0f, 87.14f)
                    curveTo(51.2f, 87.76f, 51.58f, 88.94f, 52.29f, 89.0f)
                    horizontalLineTo(52.45f)
                    curveTo(52.83f, 89.0f, 54.45f, 86.86f, 54.45f, 86.86f)
                    curveTo(54.45f, 86.86f, 83.41f, 51.74f, 86.34f, 48.0f)
                    curveTo(86.72f, 47.54f, 87.05f, 47.04f, 87.34f, 46.52f)
                    curveTo(87.41f, 46.11f, 87.38f, 45.69f, 87.24f, 45.3f)
                    curveTo(87.1f, 44.9f, 86.86f, 44.55f, 86.55f, 44.28f)
                    close()
                    moveTo(61.88f, 48.37f)
                    lineTo(74.24f, 38.12f)
                    lineTo(81.49f, 44.8f)
                    lineTo(61.88f, 48.37f)
                    close()
                    moveTo(57.08f, 47.7f)
                    lineTo(35.8f, 30.26f)
                    lineTo(70.23f, 36.61f)
                    lineTo(57.08f, 47.7f)
                    close()
                    moveTo(59.0f, 52.27f)
                    lineTo(80.78f, 48.76f)
                    lineTo(55.88f, 78.76f)
                    lineTo(59.0f, 52.27f)
                    close()
                    moveTo(32.91f, 32.0f)
                    lineTo(55.3f, 51.0f)
                    lineTo(52.06f, 78.78f)
                    lineTo(32.91f, 32.0f)
                    close()
                }
            }
        }
        .build()
        return _tron!!
    }

private var _tron: ImageVector? = null
