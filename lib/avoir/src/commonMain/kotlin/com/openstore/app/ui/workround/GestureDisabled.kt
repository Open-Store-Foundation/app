package com.openstore.app.ui.workround

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.gesturesDisabled(disabled: Boolean = true) =
    pointerInput(disabled) {
        // if gestures enabled, we don't need to interrupt
        if (!disabled) return@pointerInput

        awaitPointerEventScope {
            // we should wait for all new pointer events
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial)
                    .changes
                    .forEach(PointerInputChange::consume)
            }
        }
    }