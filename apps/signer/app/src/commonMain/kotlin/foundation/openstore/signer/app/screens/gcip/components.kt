package foundation.openstore.signer.app.screens.gcip

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.text.toAnnotatedString
import foundation.openstore.gcip.core.Challenge
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.SigningContent
import foundation.openstore.signer.app.generated.resources.firebox
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ConfirmationHeader(
    actionName: CharSequence,
    serviceName: CharSequence,
    serviceOrigin: String?,
) {
    val uriHandler = LocalUriHandler.current

    Image(
        modifier = Modifier.height(24.dp),
        imageVector = vectorResource(Res.drawable.firebox),
        contentDescription = null,
    )
    Spacer(Modifier.height(2.dp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Firebox Manager",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))

        Text(
            text = buildAnnotatedString {
                append(actionName)
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(serviceName)
                }
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        serviceOrigin?.let {
            Text(
                modifier = Modifier.clickable {
                    runCatching { uriHandler.openUri(serviceOrigin) }
                },
                text = serviceOrigin,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun SigningContent(
    challenge: Challenge
) {
    val clipboard = LocalClipboardManager.current

    SmallTitleCell(stringResource(Res.string.SigningContent))

    val transforms = challenge.transforms
    if (transforms.isNotEmpty()) {
        val transformContent = remember { transforms.joinToString { it.name } }

        SmallTitleCell("Transformation")
        AvoirBundle(
            onClick = {
                clipboard.setText(transformContent.toAnnotatedString())
            }
        ) {
            Column(
                Modifier.padding(horizontal = 19.dp, vertical = 12.dp)
            ) { Text(text = transformContent, style = MaterialTheme.typography.bodyMedium) }
        }
    }

    AvoirBundle(
        onClick = {
            clipboard.setText(challenge.displayData.toAnnotatedString())
        }
    ) {
        Column(
            Modifier.padding(horizontal = 19.dp, vertical = 12.dp)
        ) {
            Text(text = challenge.displayData, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
