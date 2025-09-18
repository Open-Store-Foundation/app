package com.openstore.app.ui.animations

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

private const val TWEEN_SPEED = 1000

@Composable
fun MultiplePulsarEffect(
    pulseCount: Int = 2,
    pulsarRadius: Float = 25f,
    pulsarColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable (Modifier) -> Unit = {}
) {
    var contentSize by remember { mutableStateOf(IntSize(0, 0)) }

    val effects: List<Pair<Float, Float>> = List(pulseCount) {
        pulsarBuilder(pulsarRadius = pulsarRadius, size = contentSize.width, delay = it * 500)
    }

    Box(
        Modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier, onDraw = {
            for (i in 0 until pulseCount) {
                val (radius, alpha) = effects[i]
                drawCircle(color = pulsarColor, radius = radius, alpha = alpha)
            }
        })
        content(
            Modifier
                .padding((pulsarRadius * 2).dp)
                .onGloballyPositioned {
                    if (it.isAttached) {
                        contentSize = it.size
                    }
                }
        )
    }
}

@Composable
private fun pulsarBuilder(pulsarRadius: Float, size: Int, delay: Int): Pair<Float, Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val radius by infiniteTransition.animateFloat(
        initialValue = (size / 2).toFloat(),
        targetValue = size + pulsarRadius,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(TWEEN_SPEED),
            initialStartOffset = StartOffset(delay),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(TWEEN_SPEED),
            initialStartOffset = StartOffset(delay + 100),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    return radius to alpha
}
