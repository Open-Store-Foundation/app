package foundation.openstore.signer.app.screens.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openstore.app.core.os.Os
import com.openstore.app.core.os.Platform
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.component.AvoirAlertDialog
import com.openstore.app.ui.component.AvoirTextButton
import com.openstore.app.ui.component.CircleIcon
import com.openstore.app.ui.lifecycle.OnResume
import com.openstore.app.ui.workround.AvoidSecureScreen
import com.openstore.app.ui.workround.rememberAvoirSheetState
import com.openstore.app.ui.workround.textLines
import foundation.openstore.kitten.android.withStatelessViewModel
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.generated.resources.CreatePasscode
import foundation.openstore.signer.app.generated.resources.DeviceNotSecure
import foundation.openstore.signer.app.generated.resources.DontRememberPasscode
import foundation.openstore.signer.app.generated.resources.EnterPasscode
import foundation.openstore.signer.app.generated.resources.ForgotPasscodeConfirm
import foundation.openstore.signer.app.generated.resources.ForgotPasscodeDescription
import foundation.openstore.signer.app.generated.resources.ForgotPasscodeTitle
import foundation.openstore.signer.app.generated.resources.PasscodeNotMatch
import foundation.openstore.signer.app.generated.resources.PasscodeTimeout
import foundation.openstore.signer.app.generated.resources.RepeatPasscode
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.pin.components.PinKeyBoard
import foundation.openstore.signer.app.screens.pin.components.PinKeyboardItem
import foundation.openstore.signer.app.screens.pin.components.PinPoints
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal const val PIN_COUNT = 5
private const val SHEET_HIDE_TARGET = 0.3f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinExtension(
    type: PinType,
    onSuccess: (SecureStore) -> Unit,
    onDismiss: () -> Unit,
) {
    val platform = remember { Os.platform() }
    when (platform) {
        Platform.Extension,
        Platform.IOS,
        Platform.Android -> PinBottomSheet(type, isExtension = true, onSuccess, onDismiss)
        else -> PinSurfaceDialog(type, onSuccess, onDismiss)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinSurfaceDialog(
    type: PinType,
    onSuccess: (SecureStore) -> Unit,
    onDismiss: () -> Unit,
) {
    DialogDisposableScope {
        Box(
            Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier.align(Alignment.TopEnd).padding(horizontal = 12.dp),
            ) {
                CircleIcon(Icons.Default.Close)
            }

            PinBottom(type, isExtension = true, onSuccess, onDismiss)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinBottomSheet(
    type: PinType,
    isExtension: Boolean = false,
    onSuccess: (SecureStore) -> Unit,
    onDismiss: () -> Unit,
) {
    DialogDisposableScope {
        val window = LocalWindowInfo.current
        var stateHolder: SheetState? = null // TODO

        val sheetState = rememberAvoirSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = {
                val state = stateHolder
                if (it == Hidden && state != null) {
                    val offset = state.requireOffset() / window.containerSize.height
                    offset > SHEET_HIDE_TARGET
                } else {
                    true
                }
            }
        )

        SideEffect {
            stateHolder = sheetState
        }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
        ) {
            PinBottom(type, isExtension, onSuccess, onDismiss)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinBottom(
    type: PinType,
    isExtension: Boolean,
    onSuccess: (SecureStore) -> Unit,
    onDismiss: () -> Unit,
) = AvoidSecureScreen {
    val viewModel: PinFeature = SignerInjector.withStatelessViewModel { providePinFeature(type) }
    val coroutineScope = rememberCoroutineScope()

    OnResume {
        viewModel.events
            .onEach { ev ->
                coroutineScope.launch {
                    when (ev) {
                        is PinEvents.Success -> {
                            ev.store?.let { onSuccess(it) }
                        }

                        is PinEvents.Cancel -> onDismiss()
                        else -> Unit
                    }
                }
            }
            .launchIn(coroutineScope)
    }

    OnResume {
        viewModel.sendAction(PinAction.OnResume)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 16.dp)
    ) {
        PinSheetContent(
            viewModel = viewModel,
            isExtension = isExtension,
            onBackspace = { viewModel.sendAction(PinAction.OnBackSpace) },
            onInput = { viewModel.sendAction(PinAction.Input(it)) },
        )
    }
}

@Composable
private fun PinSheetContent(
    viewModel: PinFeature,
    isExtension: Boolean,
    onBackspace: () -> Unit,
    onInput: (Int) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val value by viewModel.state.global.observeSafeState()
    var isForgotPasscodeDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (value.type) {
                PinType.Setup,
                PinType.Create -> if (value.isRepeat) stringResource(Res.string.RepeatPasscode) else stringResource(Res.string.CreatePasscode)
                PinType.Approve -> stringResource(Res.string.EnterPasscode)
                null -> ""
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        PinPoints(
            count = PIN_COUNT,
            events = viewModel.boxEvents,
            filled = { value.filled },
            onMatchError = { viewModel.sendAction(PinAction.ErrorResult.Match) },
            onRetry = { viewModel.sendAction(PinAction.ErrorResult.Try) },
            onTimeout = { viewModel.sendAction(PinAction.ErrorResult.Timeout) },
        )

        Spacer(modifier = Modifier.height(20.dp))

        val message = when {
            value.isPassNotMatch -> stringResource(Res.string.PasscodeNotMatch)
            !value.isSecure -> stringResource(Res.string.DeviceNotSecure)
            value.timeout > 0 -> stringResource(Res.string.PasscodeTimeout, value.timeout)
            else -> "  "
        }

        Text(
            modifier = Modifier
                .textLines(MaterialTheme.typography.bodyMedium, 1)
                .padding(horizontal = 16.dp),
            text = message,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PinKeyBoard(
            modifier = Modifier
                .width(360.dp)
                .padding(bottom = 12.dp),
            isEnabled = { value.timeout <= 0 && value.isSecure },
            isBackspaceEnabled = { value.filled > 0 },
            isFingerEnabled = value.canUseBiometry,
            isFingerVisible = value.canShowBiometry
        ) { item ->
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            when (item) {
                is PinKeyboardItem.BackSpace -> onBackspace()
                is PinKeyboardItem.FingerPrint -> {}
                is PinKeyboardItem.Number -> onInput(item.num)
            }
        }

        if (value.type?.isCreateOrSetup == false && !isExtension) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvoirTextButton(
                    title = stringResource(Res.string.DontRememberPasscode),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textStyle = MaterialTheme.typography.labelMedium,
                ) {
                    isForgotPasscodeDialogVisible = true
                }
            }
        }

        if (isForgotPasscodeDialogVisible) {
            AvoirAlertDialog(
                title = stringResource(Res.string.ForgotPasscodeTitle),
                text = stringResource(Res.string.ForgotPasscodeDescription),
                confirmText = stringResource(Res.string.ForgotPasscodeConfirm),
                onConfirm = {
                    isForgotPasscodeDialogVisible = false
                },
                onDismiss = {
                    isForgotPasscodeDialogVisible = false
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
