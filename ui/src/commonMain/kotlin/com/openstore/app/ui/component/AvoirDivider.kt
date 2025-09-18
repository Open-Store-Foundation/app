package com.openstore.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun AvoirDivider() {
    AvoirDividerCustom(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun AvoirDividerCustom(modifier: Modifier) {
    Spacer(
        modifier = modifier
            .height((1f / LocalDensity.current.density).dp) // Doesn't support Dp.Hairline
            .background(MaterialTheme.colorScheme.outline),
    )
}
