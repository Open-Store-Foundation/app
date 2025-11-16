package com.openstore.app.ui.workround

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import com.openstore.app.ui.text.emptyTextValue
import com.openstore.app.ui.text.toTextValue

@Composable
fun rememberTextFieldValue(
    changer: Any? = null,
    initValue: (() -> CharSequence?)? = null
): MutableState<TextFieldValue> {
    val state = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            initValue?.invoke()?.toTextValue()
                ?: emptyTextValue()
        )
    }

    if (changer != null && initValue != null) {
        remember(changer) {
            initValue.invoke()?.toTextValue()?.also {
                state.value = it
            }
        }
    }

    return state
}
