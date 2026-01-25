package foundation.openstore.signer.app.screens.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.cells.PropertyCell
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.text.toAnnotatedString
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.data.dao.reqSequenceId
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.SigningNum
import foundation.openstore.signer.app.generated.resources.ID
import foundation.openstore.signer.app.generated.resources.Type
import foundation.openstore.signer.app.generated.resources.Credential
import foundation.openstore.signer.app.generated.resources.DerivationPath
import foundation.openstore.signer.app.generated.resources.Curve
import foundation.openstore.signer.app.generated.resources.Service
import foundation.openstore.signer.app.generated.resources.Name
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsSheet(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.SigningNum, transaction.signing.reqSequenceId),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Text(
                    text = transaction.signing.longDateDisplay,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column {
                AvoirBundle {
                    Column {
                        PropertyCell(
                            title = stringResource(Res.string.ID),
                            value = transaction.signing.signingId.fmt,
                            onClick = { clipboard.setText(transaction.signing.signingId.fmt.toAnnotatedString()) }
                        )
                        PropertyCell(
                            title = stringResource(Res.string.Type),
                            value = transaction.signing.method.display
                        )
                    }
                }

                transaction.credential?.let { credential ->
                    SmallTitleCell(stringResource(Res.string.Credential))

                    AvoirBundle {
                        Column {
                            PropertyCell(
                                title = stringResource(Res.string.DerivationPath),
                                value = credential.derivationType.displayKey,
                                onClick = { clipboard.setText(credential.derivationType.displayKey.toAnnotatedString()) }
                            )
                            PropertyCell(
                                title = stringResource(Res.string.Curve),
                                value = credential.algorithm.displayName
                            )
                        }
                    }
                }

                SmallTitleCell(stringResource(Res.string.Service))

                AvoirBundle {
                    Column {
                        PropertyCell(
                            title = stringResource(Res.string.Name),
                            value = transaction.connection.serviceName,
                            onClick = { clipboard.setText(transaction.connection.serviceName.toAnnotatedString()) }
                        )

                        transaction.connection.serviceOrigin?.let {
                            PropertyCell(
                                title = stringResource(Res.string.ID),
                                value = it,
                                onClick = { clipboard.setText(it.toAnnotatedString()) }
                            )
                        }
                    }
                }

                transaction.signing.challenge?.let { challenge ->
                    ChallengeHistory(challenge, transaction.signing.challengeTransforms)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

