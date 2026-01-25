package foundation.openstore.signer.app.screens.import

import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.signer.app.data.mnemonic.MnemonicRepository
import foundation.openstore.signer.app.data.mnemonic.MnemonicWordsRepository
import foundation.openstore.signer.app.data.passcode.PasscodeRepository
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.data.wallet.WalletInteractor

sealed interface ImportWalletAction : MviAction {
    object Init : ImportWalletAction
    data class Import(
        val name: String,
        val mnemonic: String,
        val store: SecureStore
    ) : ImportWalletAction
}

sealed interface ImportWalletEvent {
    data object Success : ImportWalletEvent
    data object Error : ImportWalletEvent
    data object MnemonicError : ImportWalletEvent
}

data class ImportWalletState(
    val isLoading: Boolean = false,
    val hasPasscode: Boolean = false,
) : MviState

class ImportWalletViewState(
    val isLoading: MviProperty<Boolean>,
    val hasPasscode: MviProperty<Boolean>,
) : MviViewState

class ImportWalletFeature(
    private val walletInteractor: WalletInteractor,
    private val passcodeRepository: PasscodeRepository,
    private val mnemonicRepository: MnemonicRepository,
) : MviFeature<ImportWalletAction, ImportWalletState, ImportWalletViewState>(
    initAction = ImportWalletAction.Init,
    initState = ImportWalletState()
) {

    private val relay = MviRelay<ImportWalletEvent>()
    val events = relay.events

    override fun createViewState(): ImportWalletViewState {
        return buildViewState {
            ImportWalletViewState(
                isLoading = mviProperty { it.isLoading },
                hasPasscode = mviProperty { it.hasPasscode },
            )
        }
    }

    override suspend fun executeAction(action: ImportWalletAction) {
        when (action) {
            is ImportWalletAction.Init -> {
                val hasPasscode = passcodeRepository.has()
                setState { copy(hasPasscode = hasPasscode) }
            }
            is ImportWalletAction.Import -> importWallet(action.name, action.mnemonic, action.store)
        }
    }

    private suspend fun importWallet(name: String, mnemonic: String, store: SecureStore) {
        if (name.length < NAME_MIN_LENGTH) {
            relay.emit(ImportWalletEvent.Error)
            return
        }

        setState { copy(isLoading = true) }

        try {
            val result = mnemonicRepository.validate(mnemonic)
            if (result == null) {
                relay.emit(ImportWalletEvent.MnemonicError)
                return
            }

            walletInteractor.importWallet(
                name = name.trim(),
                mnemonic = result,
                isVerified = true,
                store = store
            )

            relay.emit(ImportWalletEvent.Success)
        } catch (e: Exception) {
            L.e(e)
            relay.emit(ImportWalletEvent.Error)
        } finally {
            setState { copy(isLoading = false) }
        }
    }
}

