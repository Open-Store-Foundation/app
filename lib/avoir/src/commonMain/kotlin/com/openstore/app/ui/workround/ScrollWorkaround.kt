package com.openstore.app.ui.workround

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

fun Modifier.nestedScrollHideKeyboardOnScroll(): Modifier = composed {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollConnection = remember(keyboardController) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                keyboardController?.hide()
                return super.onPreScroll(available, source)
            }
        }
    }

    nestedScroll(scrollConnection)
}
