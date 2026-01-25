package foundation.openstore.signer.app.screens.transactions

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import kotlinx.coroutines.async

sealed interface TransactionsAction : MviAction {
    data object Init : TransactionsAction
}

data class TransactionsState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
) : MviState

class TransactionsViewState(
    val global: MviProperty<TransactionsState>
) : MviViewState

class TransactionsFeature(
    private val walletId: GcipId,
    private val walletInteractor: WalletInteractor,
) : MviFeature<TransactionsAction, TransactionsState, TransactionsViewState>(
    initState = TransactionsState(),
    initAction = TransactionsAction.Init,
) {

    override fun createViewState(): TransactionsViewState {
        return buildViewState {
            TransactionsViewState(mviProperty { it })
        }
    }

    override suspend fun executeAction(action: TransactionsAction) {
        when (action) {
            is TransactionsAction.Init -> loadTransactions()
        }
    }

    private suspend fun loadTransactions() {
        val transactionsTask = bgScope.async { walletInteractor.findTransactionsBy(walletId) }
        val transactions = transactionsTask.await()

        setState {
            copy(
                transactions = transactions,
                isLoading = false,
            )
        }
    }
}

