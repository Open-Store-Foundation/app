package foundation.openstore.signer.app.screens.mnemonic.viewer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.openstore.app.core.config.BuildConfig
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.cells.AvoirDescriptionCell
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirTextButton
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.text.toAnnotatedString
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.kitten.android.withViewModel
import foundation.openstore.signer.app.generated.resources.Continue
import foundation.openstore.signer.app.generated.resources.CopyToClipboard
import foundation.openstore.signer.app.generated.resources.Mnemonic
import foundation.openstore.signer.app.generated.resources.MnemonicDescription
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.Verify
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.mnemonic.component.MnemonicWordItem
import foundation.openstore.signer.app.screens.pin.PinBottomSheet
import foundation.openstore.signer.app.screens.pin.PinType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MnemonicViewerScreen(
    navigator: NavHostController,
    mode: MnemonicViewerMode,
    onVerify: ((String) -> Unit),
) {
    val feature = SignerInjector.withViewModel {
        provideMnemonicViewerFeature(mode)
    }
    val state by feature.state.global.observeSafeState()
    val scope = rememberCoroutineScope()

    OnCreate {
        feature.channel.onEach { event ->
            when (event) {
                is MnemonicViewerEvent.Verify -> onVerify(event.id)
            }
        }.launchIn(scope)
    }

    var showPinSheet by remember(state.isUnlocked) {
        mutableStateOf(
            mode is MnemonicViewerMode.View && !state.isUnlocked
        )
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.Mnemonic),
                onNavigateUp = {
                    navigator.navigateUp()
                }
            )
        },
    ) {
        Column(Modifier.padding(it)) {
            Content(
                state = state,
                onContinue = { feature.sendAction(MnemonicViewerAction.Continue) }
            )
        }
    }

    if (showPinSheet) {
        PinBottomSheet(
            type = PinType.Approve,
            onSuccess = { store ->
                showPinSheet = false
                feature.sendAction(MnemonicViewerAction.Unlock(store))
            },
            onDismiss = {
                showPinSheet = false
                navigator.navigateUp()
            }
        )
    }
}

@Composable
private fun Content(
    state: MnemonicViewerState,
    onContinue: () -> Unit,
) {
    val displaySeeds = if (state.isUnlocked) state.seeds else state.placeholderSeeds
    val blurModifier = if (state.isUnlocked) Modifier else Modifier.blur(8.dp)

    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            modifier = blurModifier,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 19.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AvoirDescriptionCell(
                    text = stringResource(Res.string.MnemonicDescription),
                    cellPaddings = false
                )
            }

            val size = displaySeeds.size
            items(size) {
                MnemonicWordItem(title = "${state.invertedIndex(size, it).inc()}. ${displaySeeds[state.invertedIndex(size, it)]}")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (BuildConfig.isDebug) {
            val clipboard = LocalClipboardManager.current
            AvoirTextButton(
                modifier = Modifier,
                title = stringResource(Res.string.CopyToClipboard),
                icon = Icons.Default.ContentCopy,
                enabled = state.isUnlocked
            ) {
                clipboard.setText(state.seeds.joinToString(" ").toAnnotatedString())
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AvoirButton(
            title = when (state.mode) {
                is MnemonicViewerMode.Create -> stringResource(Res.string.Continue)
                is MnemonicViewerMode.View -> stringResource(Res.string.Verify)
            },
            onClick = onContinue
        )
    }
}
