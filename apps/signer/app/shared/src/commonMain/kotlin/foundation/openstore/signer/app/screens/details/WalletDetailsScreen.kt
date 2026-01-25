package foundation.openstore.signer.app.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.cells.AvoirCardCell
import com.openstore.app.ui.cells.EmptyFooterItemCell
import com.openstore.app.ui.cells.FooterItemCell
import com.openstore.app.ui.cells.TitleCell
import com.openstore.app.ui.component.AvoirAlertDialog
import com.openstore.app.ui.component.AvoirBadgeLayout
import com.openstore.app.ui.component.AvoirBadgedBox
import com.openstore.app.ui.component.AvoirBundleButton
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.BadgeDirection
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.DefaultTinyItemIcon
import com.openstore.app.ui.component.TextEmojiPreviewImage
import com.openstore.app.ui.component.defaultBundleType
import com.openstore.app.ui.lifecycle.OnCreate
import foundation.openstore.gcip.core.Blockchain
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.kitten.android.withViewModel
import foundation.openstore.signer.app.Router
import foundation.openstore.signer.app.data.Emoji
import foundation.openstore.signer.app.data.dao.LocalCredential
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.generated.resources.Cancel
import foundation.openstore.signer.app.generated.resources.Connections
import foundation.openstore.signer.app.generated.resources.Continue
import foundation.openstore.signer.app.generated.resources.Delete
import foundation.openstore.signer.app.generated.resources.DeleteWallet
import foundation.openstore.signer.app.generated.resources.DeleteWalletDescription
import foundation.openstore.signer.app.generated.resources.History
import foundation.openstore.signer.app.generated.resources.MnemonicDescription
import foundation.openstore.signer.app.generated.resources.NoHistoryYet
import foundation.openstore.signer.app.generated.resources.PopularChains
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.ShowAll
import foundation.openstore.signer.app.generated.resources.ShowMnemonic
import foundation.openstore.signer.app.icons.chain.Ada
import foundation.openstore.signer.app.icons.chain.Btc
import foundation.openstore.signer.app.icons.chain.ChainImage
import foundation.openstore.signer.app.icons.chain.Eth
import foundation.openstore.signer.app.icons.chain.Sol
import foundation.openstore.signer.app.icons.chain.Sui
import foundation.openstore.signer.app.icons.chain.Ton
import foundation.openstore.signer.app.icons.chain.Tron
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.components.BlockchainDetailsSheet
import foundation.openstore.signer.app.screens.components.TransactionCell
import foundation.openstore.signer.app.screens.components.TransactionDetailsSheet
import foundation.openstore.signer.app.screens.connections.cells.ConnectionCell
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailsScreen(
    navigator: NavHostController,
    onWalletDeleted: (Boolean) -> Unit,
    onTransactions: (GcipId) -> Unit,
    onConnection: (GcipId) -> Unit,
    onMnemonic: (GcipId) -> Unit,
) {
    val feature = SignerInjector.withViewModel {
        val data = it.state.toRoute<Router.WalletDetails>()
        provideWalletDetailsFeature(GcipId.from(data.walletId))
    }

    val state by feature.state.global.observeSafeState()
    val scope = rememberCoroutineScope()

    var transaction by remember { mutableStateOf<Transaction?>(null) }
    var showAllChains by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showContextMenu by remember { mutableStateOf(false) }
    var showMnemonicDialog by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedBlockchain: Map.Entry<Blockchain, LocalCredential>? by remember { mutableStateOf(null) }

    OnCreate {
        feature.events.onEach { event ->
            when (event) {
                is WalletDetailsEvent.Deleted -> onWalletDeleted(event.hasWallets)
                is WalletDetailsEvent.Error -> snackbarHostState.showSnackbar("Error, try again!")
            }
        }.launchIn(scope)
    }

    AvoirScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AvoirToolbar(
                title = "",
                onNavigateUp = { navigator.navigateUp() },
                actions = {
                    IconButton(onClick = { showContextMenu = true }) {
                        DefaultItemIcon(Icons.Outlined.MoreVert)
                    }

                    DropdownMenu(
                        expanded = showContextMenu,
                        onDismissRequest = { showContextMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(Res.string.Delete))
                            },
                            leadingIcon = {
                                DefaultItemIcon(Icons.Outlined.Delete)
                            },
                            onClick = {
                                showContextMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item("header") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvoirBadgedBox(
                        badge = {
                            AvoirBadgeLayout(parentSize = 72.dp, direction = BadgeDirection.EndBottom) {
                                Box(
                                    modifier = Modifier.clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .clickable { showEmojiPicker = true }
                                        .padding(6.dp)
                                ) {
                                    DefaultTinyItemIcon(Icons.Rounded.Edit)

                                    EmojiDialog(
                                        showEmojiPicker = showEmojiPicker,
                                        onSelect = {
                                            feature.sendAction(WalletDetailsAction.UpdateInitials(it))
                                            showEmojiPicker = false
                                        },
                                        onClose = {
                                            showEmojiPicker = false
                                        },
                                    )
                                }
                            }
                        },
                        image = {
                            TextEmojiPreviewImage(
                                text = state.wallet?.initials.orEmpty(),
                                size = 72.dp,
                                textSize = 42.sp,
                            )
                        },
                    )


                    Spacer(Modifier.height(12.dp))

                    Text(
                        state.wallet?.name.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item("secure") {
                AvoirBundleButton(
                    title = stringResource(Res.string.ShowMnemonic),
                    icon = Icons.Default.RemoveRedEye,
                ) {
                    showMnemonicDialog = true
                }
            }

            item("addresses") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TitleCell(stringResource(Res.string.PopularChains))

                    val displayedChains = remember(state.credentials, showAllChains) {
                        if (showAllChains) {
                            state.credentials
                        } else {
                            state.credentials.filter { (chain, _) ->
                                Blockchain.BasicChains.contains(chain)
                            }
                        }
                    }

                    val image: (Blockchain) -> ImageVector = { chain: Blockchain ->
                        when (chain) {
                            Blockchain.Bitcoin -> ChainImage.Btc
                            Blockchain.Ethereum -> ChainImage.Eth
                            Blockchain.Solana -> ChainImage.Sol
                            Blockchain.TON -> ChainImage.Ton
                            Blockchain.Cardano -> ChainImage.Ada
                            Blockchain.TRON -> ChainImage.Tron
                            Blockchain.Sui -> ChainImage.Sui
                        }
                    }

                    displayedChains.onEachIndexed { i, pair ->
                        AvoirCardCell(
                            title = pair.key.coinName,
                            subtitle = pair.key.derivationPath,
                            bundleType = defaultBundleType(displayedChains.size, i),
                            image = {
                                Image(
                                    modifier = Modifier.size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    imageVector = image(pair.key),
                                    contentDescription = null
                                )
                            },
                            onClick = { selectedBlockchain = pair }
                        )
                    }

                    if (!showAllChains) {
                        FooterItemCell(
                            title = stringResource(Res.string.ShowAll),
                            vector = Icons.Rounded.ExpandMore,
                            onClick = { showAllChains = true }
                        )
                    }
                }
            }

            if (state.connections.isNotEmpty()) {
                item("connections_header") {
                    TitleCell(
                        text = stringResource(Res.string.Connections),
                        onSeeAll = {
                            state.wallet?.let {
                                onConnection(it.id)
                            }
                        }
                    )
                }

                items(state.connections.take(3), key = { it.id.fmt }) { connection ->
                    ConnectionCell(
                        connection = connection,
                        onDelete = { feature.sendAction(WalletDetailsAction.DeleteConnection(connection.id)) }
                    )
                }
            }

            item("history_header") {
                TitleCell(
                    text = stringResource(Res.string.History),
                    onSeeAll = {
                        state.wallet?.let {
                            onTransactions(it.id)
                        }
                    }
                )
            }

            if (state.history.isEmpty()) {
                item("empty_history") {
                    EmptyFooterItemCell(
                        title = stringResource(Res.string.NoHistoryYet)
                    )
                }
            } else {
                items(state.history.take(3)) { tx ->
                    TransactionCell(
                        tx = tx,
                        onClick = { transaction = tx }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AvoirAlertDialog(
            title = stringResource(Res.string.DeleteWallet),
            text = stringResource(Res.string.DeleteWalletDescription),
            confirmText = stringResource(Res.string.Delete),
            cancelText = stringResource(Res.string.Cancel),
            onConfirm = {
                showDeleteDialog = false
                feature.sendAction(WalletDetailsAction.Delete)
            },
            onCancel = { showDeleteDialog = false },
            onDismiss = { showDeleteDialog = false },
            isDangerous = true
        )
    }

    if (showMnemonicDialog) {
        AvoirAlertDialog(
            title = stringResource(Res.string.ShowMnemonic),
            text = stringResource(Res.string.MnemonicDescription),
            confirmText = stringResource(Res.string.Continue),
            cancelText = stringResource(Res.string.Cancel),
            onConfirm = {
                showMnemonicDialog = false
                state.wallet?.let {
                    onMnemonic(it.id)
                }
            },
            onCancel = { showMnemonicDialog = false },
            onDismiss = { showMnemonicDialog = false },
            isDangerous = true,
        )
    }

    transaction?.let { tx ->
        TransactionDetailsSheet(
            transaction = tx,
            onDismiss = { transaction = null }
        )
    }

    selectedBlockchain?.let { (chain, cred) ->
        BlockchainDetailsSheet(
            blockchain = chain,
            credential = cred,
            onDismiss = { selectedBlockchain = null }
        )
    }
}


@Composable
fun EmojiDialog(
    showEmojiPicker: Boolean,
    onSelect: (String) -> Unit,
    onClose: () -> Unit,
) {
    DropdownMenu(
        expanded = showEmojiPicker,
        onDismissRequest = { onClose() }
    ) {
        val emojis = remember { Emoji.all }

        Box(modifier = Modifier.size(width = 320.dp, height = 320.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 40.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(emojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                onSelect(emoji)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}