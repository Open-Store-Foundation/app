package foundation.openstore.signer.app.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.component.AvoirEmptyScreen
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.kitten.android.withViewModel
import foundation.openstore.signer.app.Router
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.generated.resources.History
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.components.TransactionCell
import foundation.openstore.signer.app.screens.components.TransactionDetailsSheet
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navigator: NavHostController
) {
    val feature = SignerInjector.withViewModel {
        val data = it.state.toRoute<Router.Transactions>()
        provideTransactionsFeature(GcipId.from(data.walletId))
    }

    val state by feature.state.global.observeSafeState()
    var transaction by remember { mutableStateOf<Transaction?>(null) }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.History),
                onNavigateUp = { navigator.navigateUp() }
            )
        }
    ) { padding ->
        if (state.transactions.isEmpty() && !state.isLoading) {
            Column(Modifier.padding(padding)) {
                AvoirEmptyScreen()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.transactions) { tx ->
                    TransactionCell(
                        tx = tx,
                        onClick = { transaction = tx }
                    )
                }
            }
        }
    }

    transaction?.let { tx ->
        TransactionDetailsSheet(
            transaction = tx,
            onDismiss = { transaction = null }
        )
    }
}

