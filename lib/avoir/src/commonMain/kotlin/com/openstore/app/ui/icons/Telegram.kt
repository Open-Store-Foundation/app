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

public val CommonIcons.Telegram: ImageVector
    get() {
        if (_telegram != null) {
            return _telegram!!
        }
        _telegram = Builder(name = "Telegram", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp,
                viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF707A8A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(18.3327f, 9.9998f)
                curveTo(18.3327f, 14.6022f, 14.6017f, 18.3332f, 9.9993f, 18.3332f)
                curveTo(5.397f, 18.3332f, 1.666f, 14.6022f, 1.666f, 9.9998f)
                curveTo(1.666f, 5.3975f, 5.397f, 1.6665f, 9.9993f, 1.6665f)
                curveTo(14.6017f, 1.6665f, 18.3327f, 5.3975f, 18.3327f, 9.9998f)
                close()
                moveTo(10.2983f, 7.8185f)
                curveTo(9.4878f, 8.1556f, 7.8678f, 8.8534f, 5.4385f, 9.9118f)
                curveTo(5.044f, 10.0687f, 4.8373f, 10.2222f, 4.8185f, 10.3722f)
                curveTo(4.7867f, 10.6259f, 5.1043f, 10.7257f, 5.5368f, 10.8617f)
                curveTo(5.5956f, 10.8802f, 5.6566f, 10.8994f, 5.7191f, 10.9197f)
                curveTo(6.1445f, 11.058f, 6.7169f, 11.2198f, 7.0144f, 11.2262f)
                curveTo(7.2843f, 11.2321f, 7.5856f, 11.1208f, 7.9182f, 10.8924f)
                curveTo(10.188f, 9.3602f, 11.3596f, 8.5858f, 11.4332f, 8.5691f)
                curveTo(11.4851f, 8.5573f, 11.557f, 8.5425f, 11.6057f, 8.5858f)
                curveTo(11.6545f, 8.6291f, 11.6497f, 8.7111f, 11.6445f, 8.7331f)
                curveTo(11.6131f, 8.8673f, 10.3664f, 10.0263f, 9.7213f, 10.626f)
                curveTo(9.5202f, 10.813f, 9.3775f, 10.9457f, 9.3483f, 10.976f)
                curveTo(9.283f, 11.0438f, 9.2164f, 11.108f, 9.1524f, 11.1697f)
                curveTo(8.7571f, 11.5507f, 8.4607f, 11.8365f, 9.1688f, 12.3031f)
                curveTo(9.5091f, 12.5274f, 9.7814f, 12.7128f, 10.0531f, 12.8978f)
                curveTo(10.3498f, 13.0999f, 10.6457f, 13.3014f, 11.0286f, 13.5524f)
                curveTo(11.1261f, 13.6163f, 11.2193f, 13.6827f, 11.31f, 13.7474f)
                curveTo(11.6553f, 13.9936f, 11.9655f, 14.2147f, 12.3487f, 14.1794f)
                curveTo(12.5714f, 14.1589f, 12.8014f, 13.9495f, 12.9182f, 13.3251f)
                curveTo(13.1943f, 11.8492f, 13.7369f, 8.6515f, 13.8623f, 7.3338f)
                curveTo(13.8733f, 7.2184f, 13.8595f, 7.0706f, 13.8484f, 7.0058f)
                curveTo(13.8373f, 6.9409f, 13.8141f, 6.8485f, 13.7298f, 6.7801f)
                curveTo(13.6299f, 6.6991f, 13.4758f, 6.682f, 13.4069f, 6.6832f)
                curveTo(13.0934f, 6.6887f, 12.6125f, 6.8559f, 10.2983f, 7.8185f)
                close()
            }
        }
        .build()
        return _telegram!!
    }

private var _telegram: ImageVector? = null
