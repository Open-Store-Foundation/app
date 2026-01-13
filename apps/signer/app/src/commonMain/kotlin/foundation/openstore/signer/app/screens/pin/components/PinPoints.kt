package foundation.openstore.signer.app.screens.pin.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.RepeatableSpec
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.openstore.app.core.async.Async
import com.openstore.app.ui.animations.SpringEasing
import foundation.openstore.signer.app.screens.pin.PIN_COUNT
import foundation.openstore.signer.app.screens.pin.PinPointsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

const val PIN_ERROR_DELAY = 300L
const val PIN_SUCCESS_DELAY = 600

@Composable
internal fun PinPoints(
    count: Int,
    filled: () -> Int,
    onTimeout: () -> Unit = {},
    onRetry: () -> Unit = {},
    onMatchError: () -> Unit = {},
    events: Flow<PinPointsEvents>,
    pinColors: PinColors = defaultPinColors(),
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    var stateColor by remember { mutableStateOf(pinColors.filledColor) }

    val pins = remember(count) {
        MutableList(PIN_COUNT) {
            Animatable(1.0f)
        }
    }

    LaunchedEffect(Unit) {
        events
            .onEach { event ->
                coroutineScope.launch {
                    when (event) {
                        is PinPointsEvents.Created -> {
                            stateColor = pinColors.successColor
                        }

                        is PinPointsEvents.Success -> {
                            stateColor = pinColors.successColor
                        }

                        is PinPointsEvents.Error -> {
                            stateColor = pinColors.errorColor
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                            offsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = RepeatableSpec(
                                    1,
                                    animation = keyframes {
                                        durationMillis = PIN_SUCCESS_DELAY

                                        val offset = with(density) { 16.dp.toPx() }
                                        offset at 0 using SpringEasing
                                        0f at PIN_SUCCESS_DELAY using SpringEasing
                                    },
                                    RepeatMode.Restart
                                ),
                            )

                            when (event) {
                                PinPointsEvents.Error.Match -> onMatchError()
                                PinPointsEvents.Error.Try -> onRetry()
                                PinPointsEvents.Error.Timeout -> onTimeout()
                            }

                            stateColor = pinColors.filledColor
                        }

                        is PinPointsEvents.Input -> {
                            pins[event.index].animateTo(
                                targetValue = 1.0f,
                                animationSpec = RepeatableSpec(
                                    1,
                                    animation = keyframes {
                                        durationMillis = 400
                                        1.0f at 0 using FastOutSlowInEasing
                                        1.6f at 200 using FastOutSlowInEasing
                                        1.0f at 400 using FastOutSlowInEasing
                                    },
                                    RepeatMode.Restart
                                )
                            )
                        }
                    }
                }
            }
            .launchIn(coroutineScope + Async.globalScope().coroutineContext)
    }

    LazyRow(
        modifier = Modifier
            .graphicsLayer { translationX = offsetY.value },
        contentPadding = PaddingValues(horizontal = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = count, key = { it }) { i ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer {
                        scaleX = pins[i].value
                        scaleY = pins[i].value
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .drawBehind {
                        drawRect(
                            when {
                                i < filled.invoke() -> stateColor
                                else -> pinColors.defaultColor
                            }
                        )
                    }
            )

            if (i < count - 1) {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Immutable
data class PinColors(
    val defaultColor: Color,
    val filledColor: Color,
    val errorColor: Color,
    val successColor: Color,
)

@Composable
fun defaultPinColors(
    defaultColor: Color = MaterialTheme.colorScheme.outline,
    filledColor: Color = MaterialTheme.colorScheme.onSurface,
    errorColor: Color = MaterialTheme.colorScheme.error,
    successColor: Color = MaterialTheme.colorScheme.primary,
) = PinColors(defaultColor, filledColor, errorColor, successColor)
