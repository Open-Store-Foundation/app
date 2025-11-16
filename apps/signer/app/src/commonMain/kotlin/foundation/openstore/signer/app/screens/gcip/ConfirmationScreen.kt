package foundation.openstore.signer.app.screens.gcip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.cells.AvoirDialogButtonsCell
import com.openstore.app.ui.cells.PropertyButtonCell
import com.openstore.app.ui.cells.PropertyCell
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.component.AvoirBundle
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.CredentialRequest
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import foundation.openstore.signer.app.data.dao.LocalCredential
import foundation.openstore.signer.app.data.dao.WalletEntity
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.generated.resources.Back
import foundation.openstore.signer.app.generated.resources.Cancel
import foundation.openstore.signer.app.generated.resources.Connect
import foundation.openstore.signer.app.generated.resources.ConnectTo
import foundation.openstore.signer.app.generated.resources.Credential
import foundation.openstore.signer.app.generated.resources.Credentials
import foundation.openstore.signer.app.generated.resources.Delete
import foundation.openstore.signer.app.generated.resources.DerivationPath
import foundation.openstore.signer.app.generated.resources.DisconnectFor
import foundation.openstore.signer.app.generated.resources.Extend
import foundation.openstore.signer.app.generated.resources.ExtendFor
import foundation.openstore.signer.app.generated.resources.Name
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.Sign
import foundation.openstore.signer.app.generated.resources.SignFor
import foundation.openstore.signer.app.generated.resources.Wallet
import foundation.openstore.signer.app.screens.pin.PinExtension
import foundation.openstore.signer.app.screens.pin.PinType
import org.jetbrains.compose.resources.stringResource

private const val MAX_DER_COUNT = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    request: SignerRequest,
    isFullScreen: Boolean,
    wallet: MviProperty<WalletEntity?>,
    connection: MviProperty<ConnectionEntity?>,
    credential: MviProperty<LocalCredential?>,
    isLoading: MviProperty<Boolean>,
    onChangeWallet: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (SecureStore) -> Unit
) {
    val isLoading by isLoading.observeSafeState()
    val currentWallet by wallet.observeSafeState()
    val currentConnection by connection.observeSafeState()
    val currentCredential by credential.observeSafeState()

    var showPinSheet by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .run {
                    if (isFullScreen) {
                        fillMaxHeight()
                    } else {
                        this
                    }
                }
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (request) {
                is SignerRequest.Exchange -> {
                    // TODO handle for BLE, NFC, USB
                }
                is SignerRequest.Connect -> {
                    val walletEntity = currentWallet ?: return@Column
                    ConnectContent(
                        request = request,
                        wallet = walletEntity,
                        isFullScreen = isFullScreen,
                        isLoading = isLoading,
                        onChangeWallet = onChangeWallet,
                        onConnect = { showPinSheet = true },
                    )
                }
                is SignerRequest.Sign -> {
                    val walletEntity = currentWallet ?: return@Column
                    val connectionEntity = currentConnection ?: return@Column
                    val credential = currentCredential ?: return@Column
                    SignContent(
                        request = request,
                        wallet = walletEntity,
                        connection = connectionEntity,
                        credential = credential,
                        isFullScreen = isFullScreen,
                        isLoading = isLoading,
                        onCancel = onCancel,
                        onSign = { showPinSheet = true },
                    )
                }
                is SignerRequest.Extend -> {
                    val walletEntity = currentWallet ?: return@Column
                    val connectionEntity = currentConnection ?: return@Column
                    ExtendContent(
                        request = request,
                        wallet = walletEntity,
                        connection = connectionEntity,
                        isFullScreen = isFullScreen,
                        isLoading = isLoading,
                        onCancel = onCancel,
                        onConfirm = { showPinSheet = true }
                    )
                }
                is SignerRequest.Disconnect -> {
                    val walletEntity = currentWallet ?: return@Column
                    val connectionEntity = currentConnection ?: return@Column
                    DeleteContent(
                        wallet = walletEntity,
                        connection = connectionEntity,
                        isFullScreen = isFullScreen,
                        isLoading = isLoading,
                        onCancel = onCancel,
                        onDelete = { showPinSheet = true }
                    )
                }
            }
        }
    }

    if (showPinSheet) {
        PinExtension(
            type = PinType.Approve,
            onSuccess = { store ->
                showPinSheet = false
                onConfirm(store)
            },
            onDismiss = {
                showPinSheet = false
            }
        )
    }
}

@Composable
private fun ColumnScope.ConnectContent(
    request: SignerRequest.Connect,
    wallet: WalletEntity,
    isFullScreen: Boolean,
    isLoading: Boolean,
    onChangeWallet: () -> Unit,
    onConnect: () -> Unit
) {
    ConnectionRequestContent(
        actionName = stringResource(Res.string.ConnectTo),
        serviceName = request.clientData.name,
        serviceOrigin = request.clientData.origin,
        walletName = wallet.name,
        requests = request.derivations,
        isFullScreen = isFullScreen,
        negativeText = stringResource(Res.string.Back),
        positiveText = stringResource(Res.string.Connect),
        onNegative = onChangeWallet,
        onPositive = onConnect,
        isLoading = isLoading,
    )
}

@Composable
private fun ColumnScope.SignContent(
    request: SignerRequest.Sign,
    wallet: WalletEntity,
    connection: ConnectionEntity,
    credential: LocalCredential,
    isFullScreen: Boolean,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onSign: () -> Unit
) {
    ConfirmationHeader(
        actionName = stringResource(Res.string.SignFor),
        serviceName = connection.serviceName,
        serviceOrigin = connection.serviceOrigin
    )


    SmallTitleCell(stringResource(Res.string.Wallet))
    AvoirBundle {
        PropertyCell(stringResource(Res.string.Name), wallet.name)
    }

    SmallTitleCell(stringResource(Res.string.Credential))
    AvoirBundle {
        Column {
            PropertyCell(stringResource(Res.string.DerivationPath), credential.derivationType.displayKey)
        }
    }

    SigningContent(request.challenge)

    Footer(
        negative = stringResource(Res.string.Cancel),
        positive = stringResource(Res.string.Sign),
        isFullScreen = isFullScreen,
        onNegative = onCancel,
        onPositive = onSign,
        isLoading = isLoading
    )
}

@Composable
private fun ColumnScope.ExtendContent(
    request: SignerRequest.Extend,
    wallet: WalletEntity,
    connection: ConnectionEntity,
    isFullScreen: Boolean,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    ConnectionRequestContent(
        actionName = stringResource(Res.string.ExtendFor),
        serviceName = connection.serviceName,
        serviceOrigin = connection.serviceOrigin,
        walletName = wallet.name,
        requests = request.derivations,
        isFullScreen = isFullScreen,
        negativeText = stringResource(Res.string.Cancel),
        positiveText = stringResource(Res.string.Extend),
        onNegative = onCancel,
        isLoading = isLoading,
        onPositive = onConfirm,
    )
}

@Composable
private fun ColumnScope.DeleteContent(
    wallet: WalletEntity,
    connection: ConnectionEntity,
    isFullScreen: Boolean,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    ConfirmationHeader(
        actionName = stringResource(Res.string.DisconnectFor),
        serviceName = connection.serviceName,
        serviceOrigin = connection.serviceOrigin,
    )

    SmallTitleCell(stringResource(Res.string.Wallet))
    AvoirBundle {
        PropertyCell(stringResource(Res.string.Name), wallet.name)
    }

    Footer(
        negative = stringResource(Res.string.Cancel),
        positive = stringResource(Res.string.Delete),
        isFullScreen = isFullScreen,
        onNegative = onCancel,
        onPositive = onDelete,
        isDangerous = true,
        isLoading = isLoading,
    )
}

@Composable
private fun ColumnScope.ConnectionRequestContent(
    actionName: String,
    serviceName: String,
    serviceOrigin: String?,
    walletName: String,
    requests: List<CredentialRequest>,
    isFullScreen: Boolean,
    isLoading: Boolean,
    negativeText: String,
    positiveText: String,
    onNegative: () -> Unit,
    onPositive: () -> Unit
) {
    ConfirmationHeader(
        actionName = actionName,
        serviceName = serviceName,
        serviceOrigin = serviceOrigin,
    )

    SmallTitleCell(stringResource(Res.string.Wallet))
    AvoirBundle {
        PropertyCell(stringResource(Res.string.Name), walletName)
    }

    SmallTitleCell(stringResource(Res.string.Credentials))
    AvoirBundle {
        Column {
            requests.fastForEachIndexed { id, req ->
                key(id) {
                    val creds = req.credentials

                    var isExpanded by remember(creds) {
                        mutableStateOf(creds.size <= MAX_DER_COUNT)
                    }

                    val list by remember(isExpanded) {
                        if (isExpanded) {
                            mutableStateOf(creds)
                        } else {
                            mutableStateOf(creds.take(MAX_DER_COUNT))
                        }
                    }

                    list.fastForEachIndexed { i, type ->
                        key(i) {
                            when (type) {
                                is Derivation.Algo -> PropertyCell("#$i", type.algo.displayName)
                                is Derivation.Blob,
                                is Derivation.Path -> PropertyCell("#$i", type.displayKey)
                            }
                        }
                    }

                    if (!isExpanded) {
                        PropertyButtonCell(title = "Show All (${creds.size})") { isExpanded = true }
                    }
                }
            }
        }
    }

    Footer(
        positive = positiveText,
        negative = negativeText,
        isFullScreen = isFullScreen,
        onNegative = onNegative,
        onPositive = onPositive,
        isLoading = isLoading,
    )
}


@Composable
private fun ColumnScope.Footer(
    positive: String,
    negative: String,
    isFullScreen: Boolean,
    onNegative: () -> Unit,
    onPositive: () -> Unit,
    isLoading: Boolean = false,
    isDangerous: Boolean = false,
) {
    Spacer(Modifier.height(24.dp))

    if (isFullScreen) {
        Spacer(Modifier.weight(1f))
    }

    AvoirDialogButtonsCell(
        negative = negative,
        positive = positive,
        onNegative = onNegative,
        onPositive = onPositive,
        isDangerous = isDangerous,
        isNegativeEnabled = !isLoading,
        isPositiveEnabled = !isLoading,
        isLoading = isLoading,
    )
    Spacer(Modifier.height(12.dp))
}
