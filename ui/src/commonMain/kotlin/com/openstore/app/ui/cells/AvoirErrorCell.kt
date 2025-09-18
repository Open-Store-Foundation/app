package com.openstore.app.ui.cells

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.openstore.app.ui.component.AvoirOutlineButtonSmall

@Composable
fun AvoirErrorCell(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    AvoirOutlineButtonSmall(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onRetry,
    ) {
        Text(
            text = buildAnnotatedString {
                append("Error during loading, tap to")
                append(" ")

                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("try again")
                }
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
