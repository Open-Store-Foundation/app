package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirButtonDefaults
import com.openstore.app.ui.component.AvoirButtonSmall
import com.openstore.app.ui.component.AvoirTextButton

@Composable
fun AvoirButtonsCell(
    positive: String,
    negative: String? = null,
    isPositiveEnabled: Boolean = true,
    isNegativeEnabled: Boolean = true,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 19.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        negative?.let {
            AvoirTextButton(
                modifier = Modifier.weight(1f)
                    .defaultMinSize(minHeight = 47.dp),
                title = negative,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onNegative,
                enabled = isNegativeEnabled
            )

            Spacer(Modifier.width(16.dp))
        }

        AvoirButton(
            modifier = Modifier.weight(1f),
            title = positive,
            withPaddings = false,
            onClick = onPositive,
            enabled = isPositiveEnabled
        )
    }
}

@Composable
fun AvoirDialogButtonsCell(
    positive: String,
    negative: String? = null,
    isPositiveEnabled: Boolean = true,
    isNegativeEnabled: Boolean = true,
    isDangerous: Boolean = false,
    isLoading: Boolean = false,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 19.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        negative?.let {
            AvoirTextButton(
                title = negative,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onNegative,
                enabled = isNegativeEnabled,
            )

            Spacer(Modifier.width(16.dp))
        }

        AvoirButtonSmall(
            title = positive,
            onClick = onPositive,
            loading = isLoading,
            enabled = isPositiveEnabled,
            colors = when (isDangerous) {
                true -> AvoirButtonDefaults.errorButtonColors()
                else -> AvoirButtonDefaults.primaryButtonColors()
            }
        )
    }
}