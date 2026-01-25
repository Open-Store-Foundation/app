package foundation.openstore.signer.app.screens.details

import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.gcip.core.Blockchain
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import foundation.openstore.signer.app.data.dao.LocalCredential
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.data.dao.WalletEntity
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import kotlinx.coroutines.async

sealed interface WalletDetailsAction : MviAction {
    data object Init : WalletDetailsAction
    data object Delete : WalletDetailsAction
    data class DeleteConnection(val connectionId: GcipId) : WalletDetailsAction
    data class UpdateInitials(val initials: String) : WalletDetailsAction
}

sealed interface WalletDetailsEvent {
    class Deleted(val hasWallets: Boolean) : WalletDetailsEvent
    data object Error : WalletDetailsEvent
}

data class WalletDetailsState(
    val wallet: WalletEntity? = null,
    val isLoading: Boolean = true,
    val history: List<Transaction> = emptyList(),
    val connections: List<ConnectionEntity> = emptyList(),
    val credentials: Map<Blockchain, LocalCredential> = emptyMap(),
) : MviState

private const val MAX_TX_PREVIEW = 5
private const val MAX_CONNECTIONS_PREVIEW = 3

class WalletDetailsViewState(
    val global: MviProperty<WalletDetailsState>
) : MviViewState

class WalletDetailsFeature(
    private val walletId: GcipId,
    private val walletInteractor: WalletInteractor,
) : MviFeature<WalletDetailsAction, WalletDetailsState, WalletDetailsViewState>(
    initState = WalletDetailsState(),
    initAction = WalletDetailsAction.Init,
) {

    private val relay = MviRelay<WalletDetailsEvent>()
    val events = relay.events

    override fun createViewState(): WalletDetailsViewState {
        return buildViewState {
            WalletDetailsViewState(mviProperty { it })
        }
    }

    override suspend fun executeAction(action: WalletDetailsAction) {
        when (action) {
            is WalletDetailsAction.Init -> loadDetails()
            is WalletDetailsAction.Delete -> deleteWallet()
            is WalletDetailsAction.DeleteConnection -> deleteConnection(action.connectionId)
            is WalletDetailsAction.UpdateInitials -> updateInitials(action.initials)
        }
    }

    private suspend fun updateInitials(initials: String) {
        try {
            val wallet = obtainState().wallet ?: return
            val updatedWallet = wallet.copy(initials = initials)
            walletInteractor.updateWallet(updatedWallet)
            setState { copy(wallet = updatedWallet) }
        } catch (e: Throwable) {
            L.e(e)
            relay.emit(WalletDetailsEvent.Error)
        }
    }

    private suspend fun deleteConnection(connectionId: GcipId) {
        walletInteractor.disconnect(connectionId)
        setState {
            copy(connections = connections.filter { it.id != connectionId })
        }
    }

    private suspend fun deleteWallet() {
        try {
            walletInteractor.deleteWallet(walletId)
            val hasWallets = walletInteractor.hasWallets()
            relay.emit(WalletDetailsEvent.Deleted(hasWallets))
        } catch (e: Throwable) {
            L.e(e)
            relay.emit(WalletDetailsEvent.Error)
        }
    }

    private suspend fun loadDetails() {
        try {
            val walletsTask = bgScope.async { walletInteractor.findWalletBy(walletId) }
            val credentialsTask = bgScope.async { walletInteractor.findBaseCredentialsBy(walletId) }
            val historyTask = bgScope.async { walletInteractor.findTransactionsBy(walletId, MAX_TX_PREVIEW) }
            val connectionsTask = bgScope.async { walletInteractor.findConnectionsBy(walletId, MAX_CONNECTIONS_PREVIEW) }

            val wallet = walletsTask.await()
                ?: throw IllegalStateException("Wallet not found")
            val credentials = credentialsTask.await()
            val history = historyTask.await()
            val connections = connectionsTask.await()

            setState {
                copy(
                    wallet = wallet,
                    credentials = credentials,
                    history = history,
                    connections = connections,
                    isLoading = false,
                )
            }
        } catch (e: Throwable) {
            L.e(e)
            setState { copy(isLoading = false) }
            relay.emit(WalletDetailsEvent.Error)
        }
    }
}
