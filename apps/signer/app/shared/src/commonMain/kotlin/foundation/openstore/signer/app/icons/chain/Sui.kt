package foundation.openstore.signer.app.icons.chain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ChainImage.Sui: ImageVector
    get() {
        if (_sui != null) {
            return _sui!!
        }
        _sui = Builder(name = "Sui", defaultWidth = 597.0.dp, defaultHeight = 597.0.dp,
                viewportWidth = 597.0f, viewportHeight = 597.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFF4DA2FF)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = EvenOdd) {
                    moveTo(389.1f, 265.9f)
                    curveTo(404.7f, 285.5f, 414.1f, 310.4f, 414.1f, 337.4f)
                    curveTo(414.1f, 364.4f, 404.5f, 390.0f, 388.4f, 409.8f)
                    lineTo(387.0f, 411.5f)
                    lineTo(386.6f, 409.3f)
                    curveTo(386.3f, 407.5f, 385.9f, 405.6f, 385.5f, 403.7f)
                    curveTo(377.5f, 368.4f, 351.3f, 338.1f, 308.1f, 313.5f)
                    curveTo(279.0f, 297.0f, 262.3f, 277.1f, 257.9f, 254.5f)
                    curveTo(255.1f, 239.9f, 257.2f, 225.2f, 261.2f, 212.6f)
                    curveTo(265.3f, 200.0f, 271.3f, 189.5f, 276.4f, 183.2f)
                    lineTo(293.2f, 162.7f)
                    curveTo(296.1f, 159.1f, 301.7f, 159.1f, 304.6f, 162.7f)
                    lineTo(389.1f, 265.9f)
                    close()
                    moveTo(415.6f, 245.4f)
                    lineTo(303.2f, 108.0f)
                    curveTo(301.1f, 105.4f, 297.0f, 105.4f, 294.9f, 108.0f)
                    lineTo(182.4f, 245.4f)
                    lineTo(182.0f, 245.9f)
                    curveTo(161.4f, 271.6f, 149.0f, 304.2f, 149.0f, 339.7f)
                    curveTo(149.0f, 422.4f, 216.2f, 489.5f, 299.0f, 489.5f)
                    curveTo(381.8f, 489.5f, 449.0f, 422.4f, 449.0f, 339.7f)
                    curveTo(449.0f, 304.2f, 436.6f, 271.6f, 415.9f, 245.9f)
                    lineTo(415.6f, 245.4f)
                    close()
                    moveTo(209.3f, 265.5f)
                    lineTo(219.3f, 253.2f)
                    lineTo(219.6f, 255.5f)
                    curveTo(219.8f, 257.3f, 220.1f, 259.1f, 220.5f, 260.9f)
                    curveTo(227.0f, 295.0f, 250.3f, 323.5f, 289.1f, 345.5f)
                    curveTo(322.9f, 364.7f, 342.5f, 386.8f, 348.2f, 411.1f)
                    curveTo(350.6f, 421.2f, 351.0f, 431.2f, 350.0f, 439.9f)
                    lineTo(349.9f, 440.4f)
                    lineTo(349.4f, 440.6f)
                    curveTo(334.2f, 448.0f, 317.0f, 452.2f, 298.9f, 452.2f)
                    curveTo(235.4f, 452.2f, 183.9f, 400.8f, 183.9f, 337.4f)
                    curveTo(183.9f, 310.2f, 193.4f, 285.1f, 209.3f, 265.5f)
                    close()
                }
            }
        }
        .build()
        return _sui!!
    }

private var _sui: ImageVector? = null
