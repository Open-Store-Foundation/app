package foundation.openstore.signer.app.screens.import

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.openstore.app.ui.cells.AvoirTextFieldCell
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.lifecycle.OnPause
import com.openstore.app.ui.lifecycle.OnResume
import com.openstore.app.ui.workround.rememberTextFieldValue
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.pin.PinBottomSheet
import foundation.openstore.signer.app.screens.pin.PinType
import foundation.openstore.kitten.android.withViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.stringResource
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.ImportWallet
import foundation.openstore.signer.app.generated.resources.WalletName
import foundation.openstore.signer.app.generated.resources.WalletNameDescription
import foundation.openstore.signer.app.generated.resources.MnemonicPhrase
import foundation.openstore.signer.app.generated.resources.MnemonicPhraseDescription
import foundation.openstore.signer.app.generated.resources.Import

internal val NAME_MIN_LENGTH = 4
internal val NAME_MAX_LENGTH = 15

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportWalletScreen(
    navigator: NavHostController,
    onImported: () -> Unit
) {
    val feature = SignerInjector.withViewModel {
        provideImportWalletFeature()
    }
    val hasPasscode by feature.state.hasPasscode.observeSafeState()
    val scope = rememberCoroutineScope()

    var name by rememberTextFieldValue()
    var seeds by rememberTextFieldValue()
    var showPinSheet by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OnCreate {
        feature.events.onEach { event ->
            when (event) {
                is ImportWalletEvent.Success -> onImported()
                is ImportWalletEvent.Error -> {
                    snackbarHostState.showSnackbar("Failed to import wallet!")
                }
                is ImportWalletEvent.MnemonicError -> {
                    snackbarHostState.showSnackbar("Mnemonic phrase is not valid!")
                }
            }
        }.launchIn(scope)
    }

    OnResume {
        keyboardController?.show()
        focusRequester.requestFocus()
    }

    OnPause {
        focusRequester.freeFocus()
        keyboardController?.hide()
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.ImportWallet),
                onNavigateUp = { navigator.navigateUp() }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) {
        Column(
            Modifier.padding(it)
                .fillMaxSize()
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Column(
                Modifier
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AvoirTextFieldCell(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = name,
                    onValueChange = { value ->
                        if (value.text.length <= NAME_MAX_LENGTH) {
                            name = value
                        }
                    },
                    label = stringResource(Res.string.WalletName),
                    supportingText = { Text(stringResource(Res.string.WalletNameDescription)) },
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.None,
                    ),
                )

                AvoirTextFieldCell(
                    value = seeds,
                    minLines = 5,
                    maxLines = 5,
                    singleLine = false,
                    onValueChange = { seeds = it },
                    label = stringResource(Res.string.MnemonicPhrase),
                    supportingText = { Text(stringResource(Res.string.MnemonicPhraseDescription)) },
                    keyboardActions = KeyboardActions(
                        onGo = {
                            focusManager.clearFocus()
                        },
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AvoirButton(
                title = stringResource(Res.string.Import),
                enabled = name.text.length >= NAME_MIN_LENGTH && seeds.text.isNotEmpty()
            ) {
                keyboardController?.hide()
                showPinSheet = true
            }
        }
    }

    if (showPinSheet) {
        PinBottomSheet(
            type = if (hasPasscode) PinType.Approve else PinType.Create,
            onSuccess = { store ->
                showPinSheet = false
                feature.sendAction(
                    ImportWalletAction.Import(
                        name = name.text,
                        mnemonic = seeds.text,
                        store = store
                    )
                )
            },
            onDismiss = {
                showPinSheet = false
            }
        )
    }
}

