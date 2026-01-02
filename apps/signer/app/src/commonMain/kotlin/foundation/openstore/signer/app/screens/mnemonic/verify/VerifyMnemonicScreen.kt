package foundation.openstore.signer.app.screens.mnemonic.verify

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.cells.AvoirButtonsCell
import com.openstore.app.ui.cells.AvoirDescriptionCell
import com.openstore.app.ui.component.AvoirAlertDialog
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.lifecycle.OnCreate
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.kitten.android.withViewModel
import foundation.openstore.signer.app.Router
import foundation.openstore.signer.app.data.wallet.PendingAction
import foundation.openstore.signer.app.generated.resources.Cancel
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.Skip
import foundation.openstore.signer.app.generated.resources.SkipVerification
import foundation.openstore.signer.app.generated.resources.SkipVerificationDescription
import foundation.openstore.signer.app.generated.resources.TryAgain
import foundation.openstore.signer.app.generated.resources.VerificationFailed
import foundation.openstore.signer.app.generated.resources.VerificationFailedDescription
import foundation.openstore.signer.app.generated.resources.Verify
import foundation.openstore.signer.app.generated.resources.VerifyDescription
import foundation.openstore.signer.app.generated.resources.WordNum
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.mnemonic.component.MnemonicWordItem
import foundation.openstore.signer.app.screens.pin.PinBottomSheet
import foundation.openstore.signer.app.screens.pin.PinType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyMnemonicScreen(
    navigator: NavHostController,
    onSuccess: (isRefresh: Boolean) -> Unit,
) {
    val feature = SignerInjector.withViewModel {
        val data = it.state.toRoute<Router.MnemonicVerify>()
        provideVerifyMnemonicFeature(data.pendingId)
    }
    val state by feature.state.global.observeSafeState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showPinSheet by remember { mutableStateOf(false) }
    var pendingVerified by remember { mutableStateOf(true) }

    OnCreate {
        feature.channel.onEach { event ->
            when (event) {
                is VerifyMnemonicEvent.CreatingError -> {
                    snackbarHostState.showSnackbar("Can't create wallet, try again or update application!")
                }
                is VerifyMnemonicEvent.UnknownWallet -> {
                    snackbarHostState.showSnackbar("Time is up! Please try again!")
                    onSuccess(true)
                }
                is VerifyMnemonicEvent.Created -> onSuccess(true)
                is VerifyMnemonicEvent.Verified -> onSuccess(false)
            }
        }.launchIn(scope)
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.Verify),
                onNavigateUp = {
                    navigator.navigateUp()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(Modifier.padding(it)) {
            Content(
                seeds = state.seeds,
                canSkip = state.action is PendingAction.Create,
                onSuccess = {
                    val action = state.action
                    if (action is PendingAction.Verify) {
                        feature.sendAction(VerifyMnemonicAction.FinishVerify(action = action))
                    } else {
                        pendingVerified = true
                        showPinSheet = true
                    }
                },
                onSkip = {
                    pendingVerified = false
                    showPinSheet = true
                },
            )
        }
    }

    if (showPinSheet) {
        PinBottomSheet(
            type = if (state.hasPasscode) PinType.Approve else PinType.Create,
            onSuccess = { store ->
                showPinSheet = false
                feature.sendAction(VerifyMnemonicAction.FinishCreate(isVerified = pendingVerified, store = store) )
            },
            onDismiss = {
                showPinSheet = false
            }
        )
    }
}

@Composable
private fun Content(
    seeds: List<String>,
    canSkip: Boolean,
    onSuccess: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        val shuffledWords = remember(seeds) {
            if (seeds.isEmpty()) {
                emptyList()
            } else {
                seeds.chunked(3)
                    .map { it.shuffled() }
                    .flatten()
            }
        }

        val indexes = remember { mutableStateMapOf<Int, Int>() }

        LazyVerticalGrid(
            columns = GridCells.Fixed(MnemonicDefaults.COLUMNS_NUM),
            contentPadding = PaddingValues(horizontal = 19.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item("header", span = { GridItemSpan(maxLineSpan) }) {
                AvoirDescriptionCell(
                    text = stringResource(Res.string.VerifyDescription),
                    cellPaddings = false,
                )
            }

            items(
                count = seeds.size + seeds.size / 3,
                span = {
                    val id = it
                    if (id % 4 == 0) {
                        GridItemSpan(maxLineSpan)
                    } else {
                        GridItemSpan(1)
                    }
                }
            ) {
                val id = it
                val headerId = MnemonicDefaults.headerId(id)
                val targetId = MnemonicDefaults.targetNum(headerId)
                val itemId = MnemonicDefaults.itemId(id)

                if (id % 4 == 0) {
                    Text(
                        modifier = Modifier.padding(
                            top = 14.dp,
                            bottom = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                        ),
                        text = stringResource(Res.string.WordNum, targetId),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    MnemonicWordItem(
                        title = shuffledWords[itemId],
                        isSelected = indexes[headerId] == itemId,
                    ) {
                        indexes[headerId] = itemId
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        var isShowIncorrect by remember { mutableStateOf(false) }
        var isShowSkipConfirmation by remember { mutableStateOf(false) }

        AvoirButtonsCell(
            positive = stringResource(Res.string.Verify),
            negative = when (canSkip) {
                true -> stringResource(Res.string.Skip)
                false -> null
            },
            onPositive = {
                val isCorrect = (0 until seeds.size / MnemonicDefaults.COLUMNS_NUM).all { headerId ->
                    val selectedIndex = indexes[headerId] ?: return@all false
                    shuffledWords[selectedIndex] == seeds[headerId * MnemonicDefaults.COLUMNS_NUM]
                }
                if (isCorrect) {
                    onSuccess()
                } else {
                    isShowIncorrect = true
                }
            },
            isPositiveEnabled = indexes.size == seeds.size / MnemonicDefaults.COLUMNS_NUM,
            onNegative = {
                isShowSkipConfirmation = true
            }
        )

        if (isShowIncorrect) {
            AvoirAlertDialog(
                title = stringResource(Res.string.VerificationFailed),
                text = stringResource(Res.string.VerificationFailedDescription),
                confirmText = stringResource(Res.string.TryAgain),
                onConfirm = {
                    isShowIncorrect = false
                },
                onDismiss = {
                    isShowIncorrect = false
                },
            )
        }

        if (isShowSkipConfirmation) {
            AvoirAlertDialog(
                title = stringResource(Res.string.SkipVerification),
                text = stringResource(Res.string.SkipVerificationDescription),
                confirmText = stringResource(Res.string.Skip),
                cancelText = stringResource(Res.string.Cancel),
                onConfirm = {
                    isShowSkipConfirmation = false
                    onSkip()
                },
                onCancel = {
                    isShowSkipConfirmation = false
                },
                onDismiss = {
                    isShowSkipConfirmation = false
                },
                isDangerous = true,
            )
        }
    }
}

private object MnemonicDefaults {
    const val COLUMNS_NUM = 3

    fun headerId(item: Int) = item / (COLUMNS_NUM + 1)
    fun targetId(headerId: Int) = headerId * COLUMNS_NUM
    fun targetNum(headerId: Int) = targetId(headerId) + 1
    fun itemId(item: Int) = item - (headerId(item) + 1)
}

