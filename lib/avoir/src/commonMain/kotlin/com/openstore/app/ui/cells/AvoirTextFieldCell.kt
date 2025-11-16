package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.text.emptyTextValue

@Composable
fun AvoirTextFieldCell(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    label: String? = null,
    withClear: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    colors: TextFieldColors = AvoirTextFieldDefault.defaultAvoirTextColor(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (label != null) {
                Column(Modifier.padding(horizontal = 4.dp)) {
                    Text(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleSmall,
                        text = label,
                    )
                }
            }

            OutlinedTextField(
                value = value,
                modifier = modifier.fillMaxWidth(),
                onValueChange = onValueChange,
                leadingIcon = leadingIcon,
                trailingIcon = {
                    when (withClear) {
                        true -> {
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (value.text.isNotEmpty()) {
                                    IconButton(onClick = { onValueChange(emptyTextValue()) }) {
                                        DefaultItemIcon(
                                            size = 20.dp,
                                            vector = Icons.Rounded.Close // TODO test
                                        )
                                    }
                                }

                                trailingIcon?.invoke()
                            }
                        }

                        false -> trailingIcon?.invoke()
                    }
                },
                supportingText = null,
                placeholder = placeholder,
                isError = false,
                enabled = enabled,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                textStyle = MaterialTheme.typography.titleMedium,
                colors = colors,
            )

            supportingText?.let {
                val color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline

                Column(Modifier.padding(horizontal = 4.dp)) {
                    CompositionLocalProvider(
                        LocalContentColor provides color,
                        LocalTextStyle provides MaterialTheme.typography.bodySmall
                    ) {
                        supportingText()
                    }
                }
            }
        }
    }
}

object AvoirTextFieldDefault {

    @Composable
    fun buttonAvoirTextColor(
    ) = defaultAvoirTextColor(
        disabledTextColor = MaterialTheme.colorScheme.onSurface
    )

    @Composable
    fun defaultAvoirTextColor(
        focusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        errorTextColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
        disabledBorderColor: Color = MaterialTheme.colorScheme.outline,
        unfocusedBorderColor: Color = MaterialTheme.colorScheme.outline,
        errorBorderColor: Color = MaterialTheme.colorScheme.outline,
        errorTrailingIconColor: Color = MaterialTheme.colorScheme.error,
        focusedTrailingIconColor: Color = MaterialTheme.colorScheme.outline,
        unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.outline,
        errorSupportingTextColor: Color = MaterialTheme.colorScheme.error,
        focusedSupportingTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedSupportingTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        errorPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor: Color = MaterialTheme.colorScheme.primary,
        errorCursorColor: Color = MaterialTheme.colorScheme.primary,
    ) : TextFieldColors {
        return OutlinedTextFieldDefaults.colors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            errorTextColor = errorTextColor,
            disabledTextColor = disabledTextColor,

            focusedBorderColor = focusedBorderColor,
            disabledBorderColor = disabledBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            errorBorderColor = errorBorderColor,

            errorTrailingIconColor = errorTrailingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,

            errorSupportingTextColor = errorSupportingTextColor,
            focusedSupportingTextColor = focusedSupportingTextColor,
            unfocusedSupportingTextColor = unfocusedSupportingTextColor,

            errorPlaceholderColor = errorPlaceholderColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,

            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
        )
    }
}
