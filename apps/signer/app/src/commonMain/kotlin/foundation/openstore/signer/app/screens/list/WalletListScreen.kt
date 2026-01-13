package foundation.openstore.signer.app.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.cells.AvoirCardCell
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.AvoirVerifiedMark
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.TextEmojiPreviewImage
import com.openstore.app.ui.component.defaultBundleType
import foundation.openstore.kitten.android.withViewModel
import foundation.openstore.signer.app.data.dao.WalletEntity
import foundation.openstore.signer.app.generated.resources.AddNewWallet
import foundation.openstore.signer.app.generated.resources.ChooseOptionToAddWallet
import foundation.openstore.signer.app.generated.resources.CreateNew
import foundation.openstore.signer.app.generated.resources.ImportExisting
import foundation.openstore.signer.app.generated.resources.Unverified
import foundation.openstore.signer.app.generated.resources.OpenLedger
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.screens.SignerInjector
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListScreen(
    onWalletSelected: (WalletEntity) -> Unit,
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit,
    onSettings: () -> Unit,
) {
    val feature = SignerInjector.withViewModel {
        provideWalletListFeature()
    }

    val wallets by feature.state.wallets.observeSafeState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(Res.string.AddNewWallet), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(Res.string.ChooseOptionToAddWallet), style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(24.dp))
                AvoirCardCell(
                    title = stringResource(Res.string.CreateNew),
                    image = {
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(8.dp)
                        ) {
                            DefaultItemIcon(
                                Icons.Default.Add,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    onClick = {
                        feature.sendAction(WalletListAction.Connect)
//                        showBottomSheet = false
//                        onCreateWallet()
                    }
                )
                Spacer(Modifier.height(8.dp))
                AvoirCardCell(
                    title = stringResource(Res.string.ImportExisting),
                    image = {
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(8.dp)
                        ) {
                            DefaultItemIcon(
                                Icons.Default.Download,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        showBottomSheet = false
                        onImportWallet()
                    }
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.OpenLedger),
                actions = {
                    IconButton(onClick = {
                        onSettings.invoke()
                    }) {
                        DefaultItemIcon(
                            vector = Icons.Filled.Settings,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.defaultMinSize(76.dp, 76.dp),
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        LazyColumn(
            Modifier.padding(it),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                count = wallets.size,
                key = { index -> wallets[index].wallet.id.fmt },
            ) { index ->
                val wallet = wallets[index].wallet
                val count = wallets[index].count

                AvoirCardCell(
                    image = { TextEmojiPreviewImage(text = wallet.initials) },
                    labels = {
                        if (wallet.isVerified) {
                            AvoirVerifiedMark()
                        }
                    },
                    tag = {
                        if (!wallet.isVerified) {
                            Text(
                                text = stringResource(Res.string.Unverified),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    title = wallet.name,
                    subtitle = "$count connections",
                    bundleType = defaultBundleType(wallets.size, index),
                    onClick = { onWalletSelected(wallet) }
                )
            }
        }
    }
}
