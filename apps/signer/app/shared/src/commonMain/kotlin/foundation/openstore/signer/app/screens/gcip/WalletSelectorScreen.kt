package foundation.openstore.signer.app.screens.gcip

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.cells.AvoirCardCell
import com.openstore.app.ui.cells.AvoirLoaderCell
import com.openstore.app.ui.cells.EmptyFooterItemCell
import com.openstore.app.ui.component.AvoirTextButton
import com.openstore.app.ui.component.AvoirVerifiedMark
import com.openstore.app.ui.component.TextEmojiPreviewImage
import com.openstore.app.ui.component.defaultBundleType
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.signer.app.data.dao.WalletEntity
import foundation.openstore.signer.app.data.dao.WalletWithConnections
import foundation.openstore.signer.app.generated.resources.ConnectTo
import foundation.openstore.signer.app.generated.resources.NoWalletsFound
import foundation.openstore.signer.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

private const val DEFAULT_VISIBLE_CREDS = 4

@Composable
fun WalletSelectorScreen(
    request: SignerRequest.Connect,
    isFullScreen: Boolean,
    isLoading: MviProperty<Boolean>,
    wallets: MviProperty<List<WalletWithConnections>>,
    onWalletSelected: (WalletEntity) -> Unit
) {
    val wallets by wallets.observeSafeState()
    val loading by isLoading.observeSafeState()


    var isExpanded by rememberSaveable { mutableStateOf(isFullScreen) }
    val showingWallets = remember(isExpanded, wallets) {
        if (isExpanded) {
            wallets
        } else {
            wallets.take(DEFAULT_VISIBLE_CREDS)
        }
    }

    if (loading) {
        AvoirLoaderCell()
    } else if (wallets.isEmpty()) {
        EmptyFooterItemCell(
            title = stringResource(Res.string.NoWalletsFound),
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .run {
                    if (isFullScreen) {
                        fillMaxHeight()
                    } else {
                        this
                    }
                },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        ) {
            item("header") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ConfirmationHeader(
                        actionName = stringResource(Res.string.ConnectTo),
                        serviceName = request.clientData.name,
                        serviceOrigin = request.clientData.origin,
                    )

                    Spacer(Modifier.height(32.dp))
                }
            }

            items(
                count = showingWallets.size,
                key = { index -> showingWallets[index].wallet.id.fmt },
            ) { index ->
                val wallet = showingWallets[index].wallet
                val count = showingWallets[index].count

                AvoirCardCell(
                    modifier = Modifier
                        .animateItem(fadeInSpec = tween(600), fadeOutSpec = tween(600)),
                    image = { TextEmojiPreviewImage(text = wallet.initials) },
                    labels = {
                        if (wallet.isVerified) {
                            AvoirVerifiedMark()
                        }
                    },
                    title = wallet.name,
                    subtitle = "$count connections",
                    bundleType = defaultBundleType(showingWallets.size, index),
                    onClick = { onWalletSelected(wallet) }
                )
            }

            if (!isExpanded && wallets.size > DEFAULT_VISIBLE_CREDS) {
                item("footer") {
                    Column(
                        modifier = Modifier
                            .animateItem(fadeInSpec = tween(300), fadeOutSpec = tween(300))
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(12.dp))

                        AvoirTextButton(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 19.dp),
                            title = "Show All (${wallets.size})",
                            color = MaterialTheme.colorScheme.primary,
                            textStyle = MaterialTheme.typography.labelLarge,
                        ) {
                            isExpanded = true
                        }
                    }
                }
            }
        }
    }
}
