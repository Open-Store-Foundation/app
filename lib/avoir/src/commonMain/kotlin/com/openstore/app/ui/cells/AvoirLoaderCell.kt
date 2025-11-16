package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.AvoirDivider
import com.openstore.app.ui.component.AvoirLoader
import com.openstore.app.ui.component.AvoirSmallLoader

@Composable
fun AvoirLoaderScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AvoirLoader()
    }
}

@Composable
fun AvoirLoaderCell(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        AvoirSmallLoader()
    }
}
