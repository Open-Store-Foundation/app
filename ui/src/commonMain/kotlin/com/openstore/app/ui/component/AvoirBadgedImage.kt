package com.openstore.app.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AvoirBadgedBox(
    badge: String?,
    imageSize: Dp,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit,
    contentColor: Color = Color.White,
    backgroundColor: Color = Color.Red,
    direction: BadgeDirection = BadgeDirection.EndTop,
) {
    AvoirBadgedBox(
        badge = {
            if (!badge.isNullOrBlank()) {
                AvoirBadgeLayout(parentSize = imageSize, direction = direction) {
                    AvoirBadge(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        textColor = contentColor,
                        bgColor = backgroundColor
                    )
                }
            }
        },
        modifier, image,
    )
}

@Composable
fun AvoirBadgedBox(
    badge: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit,
) {
    BadgedBox(
        modifier = modifier,
        badge = {
            badge()
        },
        content = {
            image()
        }
    )
}

@Composable
fun AvoirBadgeLayout(
    parentSize: Dp,
    direction: BadgeDirection = BadgeDirection.EndTop,
    content: @Composable BoxScope.() -> Unit
) {
    val (x, y) = getBadgeCircleOffset(parentSize, direction)

    Box(
        modifier = Modifier.offset {
            IntOffset(-x, y)
        },
        content = content
    )
}

enum class BadgeDirection {
    EndTop,
    StartTop,
    EndBottom,
    StartBottom,
}

@Composable
fun getBadgeCircleOffset(parentSize: Dp, direction: BadgeDirection): Pair<Int, Int> {
    return with(LocalDensity.current) {
        val imagePx = parentSize.toPx()
        val halfRadius = imagePx / 2
        val sin = sin(PI / 4)
        val cos = cos(PI / 4)

        // Default has offset 4dp
        val coordY = when (direction) {
            BadgeDirection.EndBottom, BadgeDirection.StartBottom -> halfRadius + (halfRadius * sin)
            BadgeDirection.StartTop, BadgeDirection.EndTop -> halfRadius - (halfRadius * sin)
        } - 6.dp.toPx()

        val coordX = when (direction) {
            BadgeDirection.StartTop, BadgeDirection.StartBottom -> halfRadius + (halfRadius * cos)
            BadgeDirection.EndBottom, BadgeDirection.EndTop -> halfRadius - (halfRadius * cos)
        }

        coordX.toInt() to coordY.toInt()
    }
}
