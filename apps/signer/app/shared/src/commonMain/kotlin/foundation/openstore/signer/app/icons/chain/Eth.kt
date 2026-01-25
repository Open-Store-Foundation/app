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

public val ChainImage.Eth: ImageVector
    get() {
        if (_eth != null) {
            return _eth!!
        }
        _eth = Builder(name = "Eth", defaultWidth = 1639.0.dp, defaultHeight = 1647.0.dp,
                viewportWidth = 1639.0f, viewportHeight = 1647.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFF343434)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(820.1f, 184.0f)
                    lineTo(811.5f, 213.1f)
                    verticalLineTo(1057.7f)
                    lineTo(820.1f, 1066.3f)
                    lineTo(1212.1f, 834.5f)
                    lineTo(820.1f, 184.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF8C8C8C)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(820.1f, 184.0f)
                    lineTo(428.0f, 834.5f)
                    lineTo(820.1f, 1066.3f)
                    verticalLineTo(656.3f)
                    verticalLineTo(184.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF3C3C3B)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(820.1f, 1140.5f)
                    lineTo(815.2f, 1146.4f)
                    verticalLineTo(1447.3f)
                    lineTo(820.1f, 1461.4f)
                    lineTo(1212.4f, 908.9f)
                    lineTo(820.1f, 1140.5f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF8C8C8C)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(820.1f, 1461.4f)
                    verticalLineTo(1140.5f)
                    lineTo(428.0f, 908.9f)
                    lineTo(820.1f, 1461.4f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF141414)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(820.1f, 1066.3f)
                    lineTo(1212.1f, 834.5f)
                    lineTo(820.1f, 656.3f)
                    verticalLineTo(1066.3f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF393939)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(428.0f, 834.5f)
                    lineTo(820.1f, 1066.3f)
                    verticalLineTo(656.3f)
                    lineTo(428.0f, 834.5f)
                    close()
                }
            }
        }
        .build()
        return _eth!!
    }

private var _eth: ImageVector? = null
