package foundation.openstore.signer.app.screens.connections

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import kotlinx.coroutines.async

sealed interface ConnectionsAction : MviAction {
    data object Init : ConnectionsAction
    data class DeleteConnection(val connectionId: GcipId) : ConnectionsAction
}

sealed interface ConnectionsEvent {
    data object ConnectionDeleted : ConnectionsEvent
}

data class ConnectionsState(
    val connections: List<ConnectionEntity> = emptyList(),
    val isLoading: Boolean = true,
) : MviState

class ConnectionsViewState(
    val global: MviProperty<ConnectionsState>
) : MviViewState

class ConnectionsFeature(
    private val walletId: GcipId,
    private val walletInteractor: WalletInteractor,
) : MviFeature<ConnectionsAction, ConnectionsState, ConnectionsViewState>(
    initState = ConnectionsState(),
    initAction = ConnectionsAction.Init,
) {

    private val relay = MviRelay<ConnectionsEvent>()
    val events = relay.events

    override fun createViewState(): ConnectionsViewState {
        return buildViewState {
            ConnectionsViewState(mviProperty { it })
        }
    }

    override suspend fun executeAction(action: ConnectionsAction) {
        when (action) {
            is ConnectionsAction.Init -> loadConnections()
            is ConnectionsAction.DeleteConnection -> deleteConnection(action.connectionId)
        }
    }

    private suspend fun deleteConnection(connectionId: GcipId) {
        walletInteractor.disconnect(connectionId)
        setState {
            copy(connections = connections.filter { it.id != connectionId })
        }
        relay.emit(ConnectionsEvent.ConnectionDeleted)
    }

    private suspend fun loadConnections() {
        val connectionsTask = bgScope.async { walletInteractor.findConnectionsBy(walletId) }
        val connections = connectionsTask.await()

        setState {
            copy(
                connections = connections,
                isLoading = false,
            )
        }
    }
}

