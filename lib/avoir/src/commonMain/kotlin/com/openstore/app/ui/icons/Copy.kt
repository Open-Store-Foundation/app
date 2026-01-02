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

public val CommonIcons.Copy: ImageVector
    get() {
        if (`_copy-f` != null) {
            return `_copy-f`!!
        }
        `_copy-f` = Builder(name = "Copy-f", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp,
            viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF707A8A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd) {
                moveTo(7.5002f, 2.5f)
                horizontalLineTo(16.6668f)
                verticalLineTo(13.3333f)
                horizontalLineTo(14.1668f)
                verticalLineTo(5.0f)
                horizontalLineTo(7.5002f)
                verticalLineTo(2.5f)
                close()
                moveTo(3.3335f, 6.6667f)
                verticalLineTo(17.5f)
                horizontalLineTo(12.5002f)
                verticalLineTo(6.6836f)
                lineTo(3.3335f, 6.6667f)
                close()
            }
        }
            .build()
        return `_copy-f`!!
    }

private var `_copy-f`: ImageVector? = null
