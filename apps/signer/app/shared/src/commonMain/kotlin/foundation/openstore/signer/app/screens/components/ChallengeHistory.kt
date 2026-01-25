package foundation.openstore.signer.app.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.text.toAnnotatedString
import foundation.openstore.gcip.core.transport.GcipTransformAlgorithm
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.SigningContent
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChallengeHistory(
    display: String,
    transforms: List<GcipTransformAlgorithm>?
) {
    val clipboard = LocalClipboardManager.current

    SmallTitleCell(stringResource(Res.string.SigningContent))

    AvoirBundle(
        onClick = {
            clipboard.setText(display.toAnnotatedString())
        }
    ) {
        Box(Modifier.padding(16.dp)) {
            Text(
                text = display,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}