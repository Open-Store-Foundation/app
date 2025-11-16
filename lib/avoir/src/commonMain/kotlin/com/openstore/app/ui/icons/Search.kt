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

public val CommonIcons.Search: ImageVector
    get() {
        if (`_search-f` != null) {
            return `_search-f`!!
        }
        `_search-f` = Builder(name = "Search-f", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp,
                viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF707A8A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(9.1667f, 5.0f)
                curveTo(11.4679f, 5.0f, 13.3333f, 6.8655f, 13.3333f, 9.1667f)
                curveTo(13.3333f, 11.4679f, 11.4679f, 13.3333f, 9.1667f, 13.3333f)
                curveTo(6.8655f, 13.3333f, 5.0f, 11.4679f, 5.0f, 9.1667f)
                curveTo(5.0f, 6.8655f, 6.8655f, 5.0f, 9.1667f, 5.0f)
                close()
                moveTo(9.1667f, 2.5f)
                curveTo(12.8486f, 2.5f, 15.8333f, 5.4848f, 15.8333f, 9.1667f)
                curveTo(15.8333f, 10.3256f, 15.5376f, 11.4154f, 15.0175f, 12.3649f)
                lineTo(17.5763f, 14.9238f)
                lineTo(16.2505f, 16.2496f)
                lineTo(14.9248f, 17.5753f)
                lineTo(12.3663f, 15.0167f)
                curveTo(11.4166f, 15.5373f, 10.3262f, 15.8333f, 9.1667f, 15.8333f)
                curveTo(5.4848f, 15.8333f, 2.5f, 12.8486f, 2.5f, 9.1667f)
                curveTo(2.5f, 5.4848f, 5.4848f, 2.5f, 9.1667f, 2.5f)
                close()
            }
        }
        .build()
        return `_search-f`!!
    }

private var `_search-f`: ImageVector? = null
