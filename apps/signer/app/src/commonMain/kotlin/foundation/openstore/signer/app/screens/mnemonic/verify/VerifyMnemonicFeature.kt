package foundation.openstore.signer.app.screens.mnemonic.verify

import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.wallet.PendingWalletRepository
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import foundation.openstore.signer.app.data.passcode.PasscodeRepository
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.data.wallet.PendingAction

sealed interface VerifyMnemonicAction : MviAction {
    data object Init : VerifyMnemonicAction
    data class FinishCreate(val isVerified: Boolean, val store: SecureStore) : VerifyMnemonicAction
    data class FinishVerify(val action: PendingAction.Verify) : VerifyMnemonicAction
}

sealed interface VerifyMnemonicEvent {
    data object CreatingError : VerifyMnemonicEvent
    data object UnknownWallet : VerifyMnemonicEvent
    data object Created : VerifyMnemonicEvent
    data object Verified : VerifyMnemonicEvent
}

data class VerifyMnemonicState(
    val seeds: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val hasPasscode: Boolean = false,
    val action: PendingAction? = null,
) : MviState

data class VerifyMnemonicViewState(
    val global: MviProperty<VerifyMnemonicState>
) : MviViewState

class VerifyMnemonicFeature(
    private val pendingId: String,
    private val walletInteractor: WalletInteractor,
    private val pendingWalletRepository: PendingWalletRepository,
    private val passcodeRepository: PasscodeRepository,
) : MviFeature<VerifyMnemonicAction, VerifyMnemonicState, VerifyMnemonicViewState>(
    initState = VerifyMnemonicState(),
    initAction = VerifyMnemonicAction.Init,
) {

    private val relay = MviRelay<VerifyMnemonicEvent>()
    val channel = relay.events

    override fun createViewState(): VerifyMnemonicViewState {
        return buildViewState {
            VerifyMnemonicViewState(
                mviProperty { it }
            )
        }
    }

    override suspend fun executeAction(action: VerifyMnemonicAction) {
        when (action) {
            is VerifyMnemonicAction.Init -> loadMnemonic()
            is VerifyMnemonicAction.FinishCreate -> finishCreate(action.isVerified, action.store)
            is VerifyMnemonicAction.FinishVerify -> finishVerify(action.action)
        }
    }

    private suspend fun loadMnemonic() {
        val pendingAction = pendingWalletRepository.getPending(pendingId)
        if (pendingAction != null) {
            val seed = pendingAction.mnemonic.unwrap()
            val hasPasscode = passcodeRepository.has()
            setState { copy(seeds = seed, action = pendingAction, hasPasscode = hasPasscode) }
        } else {
            relay.emit(VerifyMnemonicEvent.UnknownWallet)
        }
    }

    private suspend fun finishCreate(isVerified: Boolean, store: SecureStore) {
        setState { copy(isLoading = true) }

        try {
            val action = pendingWalletRepository.getPending(pendingId)
            if (action is PendingAction.Create) {
                walletInteractor.importWallet(
                    name = action.name,
                    mnemonic = action.mnemonic,
                    isVerified = isVerified,
                    store = store,
                )

                pendingWalletRepository.clear(pendingId)
                relay.emit(VerifyMnemonicEvent.Created)
            } else {
                relay.emit(VerifyMnemonicEvent.UnknownWallet)
            }
        } catch (e: Exception) {
            L.e(e)
            relay.emit(VerifyMnemonicEvent.CreatingError)
        } finally {
            setState { copy(isLoading = false) }
        }
    }

    private suspend fun finishVerify(action: PendingAction.Verify) {
        walletInteractor.verify(action.walletId)
        pendingWalletRepository.clear(pendingId)
        relay.emit(VerifyMnemonicEvent.Verified)
    }
}
