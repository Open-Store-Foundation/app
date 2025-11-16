package foundation.openstore.signer.app.screens.mnemonic.viewer

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.mnemonic.Mnemonic
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.data.wallet.PendingWalletRepository
import foundation.openstore.signer.app.data.wallet.WalletInteractor

sealed interface MnemonicViewerMode {
    data class View(val walletId: GcipId) : MnemonicViewerMode
    data class Create(val walletName: String) : MnemonicViewerMode
}

sealed interface MnemonicViewerAction : MviAction {
    data object Init : MnemonicViewerAction
    data class Unlock(val store: SecureStore) : MnemonicViewerAction
    data object Continue : MnemonicViewerAction
}

sealed interface MnemonicViewerEvent {
    data class Verify(val id: String) : MnemonicViewerEvent
}

data class MnemonicViewerState(
    val seeds: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isUnlocked: Boolean = false,
    val mode: MnemonicViewerMode,
) : MviState {

    val placeholderSeeds: List<String>
        get() = listOf(
            "apple", "banana", "cherry", "dragon", "eagle", "forest",
            "garden", "harbor", "island", "jungle", "kitten", "lemon",
        )

    fun invertedIndex(size: Int, i: Int): Int {
        val half = size / 2
        return when (i % 2 == 0) {
            true -> i / 2
            else -> half + i / 2
        }
    }
}

class MnemonicViewerViewState(
    val global: MviProperty<MnemonicViewerState>
) : MviViewState

class MnemonicViewerFeature(
    private val mode: MnemonicViewerMode,
    private val walletInteractor: WalletInteractor,
    private val pendingWalletRepository: PendingWalletRepository,
) : MviFeature<MnemonicViewerAction, MnemonicViewerState, MnemonicViewerViewState>(
    initState = MnemonicViewerState(mode = mode),
    initAction = MnemonicViewerAction.Init,
) {

    private val relay = MviRelay<MnemonicViewerEvent>()
    val channel = relay.events

    override fun createViewState(): MnemonicViewerViewState {
        return buildViewState {
            MnemonicViewerViewState(mviProperty { it })
        }
    }

    override suspend fun executeAction(action: MnemonicViewerAction) {
        when (action) {
            is MnemonicViewerAction.Init -> {
                if (mode is MnemonicViewerMode.Create) {
                    createMnemonic()
                }
            }
            is MnemonicViewerAction.Unlock -> {
                if (mode is MnemonicViewerMode.View) {
                    loadMnemonic(mode.walletId, action.store)
                }
            }
            is MnemonicViewerAction.Continue -> {
                val state = obtainState()
                val mnemonic = Mnemonic(
                    value = state.seeds.joinToString(" ") // TODO do not create new string
                        .toCharArray()
                )

                pendingWalletRepository.clearAll()
                when (mode) {
                    is MnemonicViewerMode.Create -> {
                        val pendingId = pendingWalletRepository.putPendingCreation(mode.walletName, mnemonic)
                        relay.emit(MnemonicViewerEvent.Verify(pendingId))
                    }
                    is MnemonicViewerMode.View -> {
                        val pendingId = pendingWalletRepository.putPendingVerification(mode.walletId, mnemonic)
                        relay.emit(MnemonicViewerEvent.Verify(pendingId))
                    }
                }
            }
        }
    }

    private suspend fun createMnemonic() {
        val mnemonic = walletInteractor.createMnemonic()

        setState {
            copy(
                seeds = mnemonic.unwrap(),
                isLoading = false,
                isUnlocked = true,
            )
        }
    }

    private suspend fun loadMnemonic(walletId: GcipId, store: SecureStore) {
        val seeds = walletInteractor.getMnemonic(walletId, store)
            .use { it.unwrap() }

        setState {
            copy(
                seeds = seeds,
                isLoading = false,
                isUnlocked = true,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        pendingWalletRepository.clearAll()
    }
}
