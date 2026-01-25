package foundation.openstore.signer.app.screens.list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.dao.WalletWithConnections
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.ExperimentalAtomicApi

sealed interface WalletListAction : MviAction {
    data object LoadWallets : WalletListAction
    data object Connect : WalletListAction
}

data class WalletListState(
    val isLoading: Boolean = false,
    val indexes: MutableMap<GcipId, Int> = mutableMapOf(),
    val wallets: SnapshotStateList<WalletWithConnections> = mutableStateListOf(),
) : MviState

class WalletListViewState(
    val isLoading: MviProperty<Boolean>,
    val wallets: MviProperty<List<WalletWithConnections>>,
) : MviViewState

@OptIn(ExperimentalAtomicApi::class)
class WalletListFeature(
    private val walletInteractor: WalletInteractor,
//    private val server: BlePeripheralProvider,
) : MviFeature<WalletListAction, WalletListState, WalletListViewState>(
    initState = WalletListState(),
    initAction = WalletListAction.LoadWallets,
) {

    override fun createViewState(): WalletListViewState {
        return buildViewState {
            WalletListViewState(
                isLoading = mviProperty { it.isLoading },
                wallets = mviProperty { it.wallets },
            )
        }
    }

    override suspend fun executeAction(action: WalletListAction) {
        when (action) {
            is WalletListAction.LoadWallets -> {
                setState { copy(isLoading = true) }
                try {
                    val wallets = walletInteractor.getWallestWithConnections()
                    val walletsState = wallets.toMutableStateList()

                    val indexes = HashMap<GcipId, Int>().apply {
                        wallets.forEachIndexed { id, data ->
                            this[data.wallet.id] = id
                        }
                    }

                    setState { copy(wallets = walletsState, indexes = indexes, isLoading = false) }

                    observeData()
                } catch (e: Exception) {
                    L.e(e)
                    setState { copy(isLoading = false) }
                }
            }
            is WalletListAction.Connect -> {
//                server.startHandshake(GcipId(ByteArray(16)))
            }
        }
    }

    private fun observeData() {
        walletInteractor.onWalletChanged
            .onEach { data ->
                stateScope.launch {
                    obtainState().apply {
                        val id = indexes[data.wallet.id] ?: -1
                        when {
                            id >= 0 -> wallets[id] = data
                            else -> {
                                indexes[data.wallet.id] = wallets.size
                                wallets.add(data)
                            }
                        }
                    }
                }
            }
            .launchIn(bgScope)
    }
}

