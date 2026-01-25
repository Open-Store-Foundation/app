package foundation.openstore.signer.app.screens.mnemonic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.MnemonicWordItem(
    title: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Text(
        modifier = modifier
            .weight(1f)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            )
            .run {
                onClick
                    ?.let { clickable(onClick = it) }
                    ?: this
            }
            .padding(vertical = 12.dp, horizontal = 6.dp),
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        textAlign = TextAlign.Center
    )
}