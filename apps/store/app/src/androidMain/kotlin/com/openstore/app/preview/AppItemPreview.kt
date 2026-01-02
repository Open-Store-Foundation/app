package com.openstore.app.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.preview.ThemePreview

@Preview
@Composable
fun PreviewHorizontalCardCexType() {
    ThemePreview(isUseTheme = false) {
        Column(
            modifier = Modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth()
        ) {
            AvoirBundle {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 19.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Downloading...",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "15% (25 KB / 3.1 MB)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(),
                        progress = { 0.5f },
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )

                }
            }

//            AppDetailsScreen()
        }
    }
}

