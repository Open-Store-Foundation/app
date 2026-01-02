package foundation.openstore.signer.app.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.cells.PropertyCell
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.text.toAnnotatedString
import foundation.openstore.gcip.core.Blockchain
import foundation.openstore.signer.app.data.dao.LocalCredential
import foundation.openstore.signer.app.generated.resources.Address
import foundation.openstore.signer.app.generated.resources.Curve
import foundation.openstore.signer.app.generated.resources.DerivationPath
import foundation.openstore.signer.app.generated.resources.Info
import foundation.openstore.signer.app.generated.resources.PublicKey
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.utils.ChainAddress
import foundation.openstore.signer.app.utils.shrinkAddress
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockchainDetailsSheet(
    blockchain: Blockchain,
    credential: LocalCredential,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(Modifier.fillMaxWidth()) {
            AvoirToolbar(title = blockchain.coinName)

            Column {
                SmallTitleCell(stringResource(Res.string.Info))

                AvoirBundle {
                    Column {
                        ChainAddress.getAddress(credential.payloadRaw, blockchain)?.let {
                            PropertyCell(
                                title = stringResource(Res.string.Address),
                                value = it.shrinkAddress(),
                                onClick = { clipboard.setText(it.toAnnotatedString()) }
                            )
                        }

                        PropertyCell(
                            title = stringResource(Res.string.DerivationPath),
                            value = blockchain.derivationPath,
                            onClick = { clipboard.setText(blockchain.derivationPath.toAnnotatedString()) }
                        )
                        PropertyCell(
                            title = stringResource(Res.string.Curve),
                            value = blockchain.curve.displayName
                        )
                    }
                }

                SmallTitleCell(stringResource(Res.string.PublicKey))
                AvoirBundle(
                    onClick = {
                        clipboard.setText(credential.payloadHex.toAnnotatedString())
                    }
                ) {
                    Box(Modifier.padding(16.dp)) {
                        Text(
                            text = credential.payloadHex,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}



