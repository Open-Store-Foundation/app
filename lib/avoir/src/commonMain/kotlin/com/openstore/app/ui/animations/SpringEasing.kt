package com.openstore.app.ui.animations

import androidx.compose.animation.core.Easing
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

val SpringEasing = TensionSpringEasing()

class TensionSpringEasing(private val tension: Float = 0.40f) : Easing {
    override fun transform(fraction: Float): Float {
        return 2f.pow(-4f * fraction) * sin((fraction - tension / 4) * (2 * PI.toFloat()) / tension) + 1
    }
}
