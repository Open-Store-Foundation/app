package com.openwallet.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.cells.AvoirButtonsCell
import com.openstore.app.ui.cells.AvoirCardCell
import com.openstore.app.ui.cells.AvoirDialogButtonsCell
import com.openstore.app.ui.cells.AvoirTextCheckboxCell
import com.openstore.app.ui.cells.PropertyCell
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.TextOutlinePreviewImage
import com.openwallet.sample.di.WalletInjector
import foundation.openstore.gcip.core.Blockchain
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.Transport
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.kitten.android.withStatelessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletApp(
    responseData: ByteArray?,
    onResponseConsumed: () -> Unit,
    startIntent: (ByteArray) -> Unit,
    onMessage: (String) -> Unit,
//    bleHelper: BleHelper? = null,
) {
    val viewModel = WalletInjector.withStatelessViewModel { provideMainViewModel() }
    val wallets by viewModel.wallets.collectAsState()
    val exchangeSessions by viewModel.exchangeSessions.collectAsState()
//    val isScanning by viewModel.isScanning.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { msg ->
            onMessage(msg)
        }
    }

    LaunchedEffect(responseData) {
        if (responseData != null) {
            viewModel.handleResponseData(responseData)
            onResponseConsumed()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.requests.collect { request: ByteArray ->
            startIntent(request)
        }
    }

    LazyColumn {
        item {

            Modifier.animateItem()
        }
    }
    WalletListScreen(
        exchangeSessions = exchangeSessions,
        wallets = wallets,
        onImportWallet = { derivations ->
            viewModel.importWallet(derivations)
        },
        onRequestExchange = {
            viewModel.requestExchange(Transport.Internal)
        },
        onConnectExchangeSession = { eid, derivations ->
            viewModel.connectExchangeSession(eid, derivations)
        },
        onSign = viewModel::sign,
        onDisconnect = viewModel::disconnect,
        onExtend = viewModel::extend,
//        bleHelper = bleHelper,
//        onStartScan = viewModel::startScanning,
//        onStopScan = viewModel::stopScanning,
//        isScanning = isScanning,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListScreen(
    exchangeSessions: List<WalletSession>,
    wallets: List<Wallet>,
    onImportWallet: (List<Derivation>) -> Unit,
    onRequestExchange: () -> Unit,
    onConnectExchangeSession: (GcipId, List<Derivation>) -> Unit,
    onSign: (Wallet, CredentialInfo) -> Unit,
    onDisconnect: (Wallet) -> Unit,
    onExtend: (Wallet, List<Derivation>) -> Unit,
//    bleHelper: BleHelper? = null,
    onStartScan: () -> Unit = {},
    onStopScan: () -> Unit = {},
    isScanning: Boolean = false,
) {

    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var selectedExchangeEid by remember { mutableStateOf<GcipId?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showConnectModal by remember { mutableStateOf(false) }
    var connectModalMode by remember { mutableStateOf<ConnectModalMode>(ConnectModalMode.Import) }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                SmallTitleCell("Choose Action")

                AvoirCardCell(
                    image = {
                        TextOutlinePreviewImage(text = "+", modifier = Modifier.size(40.dp))
                    },
                    title = "Connect",
                    subtitle = "Connect a wallet",
                    onClick = {
                        showAddSheet = false
                        connectModalMode = ConnectModalMode.Import
                        showConnectModal = true
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                AvoirCardCell(
                    image = {
                        Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(40.dp))
                    },
                    title = "Exchange",
                    subtitle = "Create exchange session",
                    onClick = {
                        showAddSheet = false
                        onRequestExchange()
                    }
                )
            }
        }
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = "Sample Wallet",
                actions = {
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Import Wallet")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(exchangeSessions.size) { index ->
                val session = exchangeSessions[index]
                AvoirCardCell(
                    image = {
                        TextOutlinePreviewImage(text = "E", modifier = Modifier.size(40.dp))
                    },
                    title = "Exchange Session",
                    subtitle = session.eid.fmt,
                    onClick = { selectedExchangeEid = session.eid }
                )
            }
            items(wallets.size) { index ->
                val wallet = wallets[index]
                AvoirCardCell(
                    image = {
                         TextOutlinePreviewImage(text = "#${index + 1}", modifier = Modifier.size(40.dp))
                    },
                    title = wallet.name,
                    subtitle = wallet.id.fmt.take(8) + "...",
                    onClick = { selectedWallet = wallet }
                )
            }
        }
    }

    if (selectedExchangeEid != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedExchangeEid = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            ExchangeSessionBottomSheet(
                eid = selectedExchangeEid!!,
                onConnect = { selectedDerivations ->
                    val eid = selectedExchangeEid!!
                    selectedExchangeEid = null
                    onConnectExchangeSession(eid, selectedDerivations)
                },
                onCancel = { selectedExchangeEid = null }
            )
        }
    }

    if (selectedWallet != null) {
        val currentWallet = wallets.find { it.connectionId == selectedWallet!!.connectionId } ?: selectedWallet!!
        
        ModalBottomSheet(
            onDismissRequest = { selectedWallet = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            WalletBottomSheetContent(
                wallet = currentWallet,
                onSign = { cred ->
                    val walletToSign = currentWallet
                    selectedWallet = null
                    onSign(walletToSign, cred)
                },
                onDisconnect = {
                    val walletToDisconnect = currentWallet
                    selectedWallet = null
                    onDisconnect(walletToDisconnect)
                },
                onExtend = {
                    connectModalMode = ConnectModalMode.Extend(currentWallet)
                    selectedWallet = null
                    showConnectModal = true
                },
            )
        }
    }

    if (showConnectModal) {
        val existingDerivations = when (val mode = connectModalMode) {
            is ConnectModalMode.Extend -> mode.wallet.credentials.mapNotNull { it.derivation }.toSet()
            ConnectModalMode.Import -> emptySet()
        }

        ModalBottomSheet(
            onDismissRequest = { showConnectModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            ConnectWalletBottomSheet(
                existingDerivations = existingDerivations,
                onConnect = { selected ->
                    showConnectModal = false
                    when (val mode = connectModalMode) {
                        ConnectModalMode.Import -> onImportWallet(selected)
                        is ConnectModalMode.Extend -> onExtend(mode.wallet, selected)
                    }
                },
                onCancel = {
                    showConnectModal = false
                },
//                bleHelper = bleHelper,
                onStartScan = onStartScan,
                onStopScan = onStopScan,
                isScanning = isScanning,
            )
        }
    }
}

sealed interface ConnectModalMode {
    data object Import : ConnectModalMode
    data class Extend(val wallet: Wallet) : ConnectModalMode
}

@Composable
fun ExchangeSessionBottomSheet(
    eid: GcipId,
    onConnect: (List<Derivation>) -> Unit,
    onCancel: () -> Unit
) {
    val selectedChains = remember { mutableStateListOf<Blockchain>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        SmallTitleCell("Exchange Session")
        AvoirBundle {
            Column {
                PropertyCell("EID", eid.fmt)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SmallTitleCell("Select Networks")
        Column {
            Blockchain.entries.forEach { chain ->
                val isSelected = selectedChains.contains(chain)
                AvoirTextCheckboxCell(
                    title = chain.coinName,
                    subtitle = chain.derivationPath,
                    isChecked = isSelected,
                    enabled = true,
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedChains.add(chain)
                        } else {
                            selectedChains.remove(chain)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AvoirButtonsCell(
            positive = "Connect",
            isPositiveEnabled = selectedChains.isNotEmpty(),
            onPositive = { onConnect(selectedChains.map { it.toDerivationType() }) },
            onNegative = onCancel
        )
    }
}

@Composable
fun ConnectWalletBottomSheet(
    existingDerivations: Set<String>,
    onConnect: (List<Derivation>) -> Unit,
    onCancel: () -> Unit,
//    bleHelper: BleHelper? = null,
    onStartScan: () -> Unit = {},
    onStopScan: () -> Unit = {},
    isScanning: Boolean = false,
) {
    var step by remember { mutableStateOf(ConnectStep.SelectNetworks) }
    val selectedChains = remember { mutableStateListOf<Blockchain>() }
    var selectedDerivations by remember { mutableStateOf<List<Derivation>>(emptyList()) }

    when (step) {
        ConnectStep.SelectNetworks -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                SmallTitleCell("Select Networks")

                Column {
                    Blockchain.entries.forEachIndexed { index, chain ->
                        val isExisting = existingDerivations.contains(chain.derivationPath)
                        val isSelected = selectedChains.contains(chain)

                        AvoirTextCheckboxCell(
                            title = chain.coinName,
                            subtitle = chain.derivationPath,
                            isChecked = isExisting || isSelected,
                            enabled = !isExisting,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedChains.add(chain)
                                } else {
                                    selectedChains.remove(chain)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AvoirButtonsCell(
                    positive = "Send Request",
                    isPositiveEnabled = selectedChains.isNotEmpty(),
                    onPositive = {
                        val derivations = selectedChains.map { it.toDerivationType() }
//                        if (bleHelper != null) {
//                            selectedDerivations = derivations
//                            step = ConnectStep.SelectTransport
//                        } else {
                            onConnect(derivations)
//                        }
                    },
                    onNegative = {}
                )
            }
        }
        ConnectStep.SelectTransport -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                SmallTitleCell("Select Transport")
                
                Spacer(modifier = Modifier.height(16.dp))

                AvoirCardCell(
                    image = { TextOutlinePreviewImage(text = "I", modifier = Modifier.size(40.dp)) },
                    title = "Internal",
                    subtitle = "Use internal transport (Intent)",
                    onClick = { onConnect(selectedDerivations) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                AvoirCardCell(
                    image = { TextOutlinePreviewImage(text = "B", modifier = Modifier.size(40.dp)) },
                    title = "BLE",
                    subtitle = "Use Bluetooth Low Energy",
                    onClick = { step = ConnectStep.BleScan }
                )
            }
        }
        ConnectStep.BleScan -> {
//            BleConnectionScreen(
//                bleHelper = bleHelper!!,
//                onStartScan = onStartScan,
//                onStopScan = onStopScan,
//                isScanning = isScanning,
//                onCancel = onCancel
//            )
        }
    }
}

private enum class ConnectStep {
    SelectNetworks, SelectTransport, BleScan
}



@Composable
fun WalletBottomSheetContent(
    wallet: Wallet,
    onSign: (CredentialInfo) -> Unit,
    onDisconnect: () -> Unit,
    onExtend: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        SmallTitleCell("Info")
        AvoirBundle {
            Column {
                PropertyCell("Name", wallet.name)
                PropertyCell("Connection ID", wallet.connectionId.fmt)
                PropertyCell("Verified", wallet.isVerified.toString())
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        SmallTitleCell("Derivations")
        AvoirBundle {
            Column {
                wallet.credentials.forEachIndexed { index, cred ->
                    key(cred.id) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSign(cred) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = cred.derivation ?: "Unknown path", style = MaterialTheme.typography.bodyMedium)
                            Text(text = cred.id.fmt.take(8) + "...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        AvoirDialogButtonsCell(
            positive = "Extend",
            negative = "Disconnect",
            onNegative = onDisconnect,
            onPositive = onExtend,
        )
    }
}
