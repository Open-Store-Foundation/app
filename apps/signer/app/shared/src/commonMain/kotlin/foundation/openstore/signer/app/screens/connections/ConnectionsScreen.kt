package foundation.openstore.signer.app.screens.connections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.component.AvoirEmptyScreen
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.Router
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.signer.app.screens.connections.cells.ConnectionCell
import foundation.openstore.kitten.android.withViewModel
import org.jetbrains.compose.resources.stringResource
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.Connections

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsScreen(
    navigator: NavHostController
) {
    val feature = SignerInjector.withViewModel {
        val data = it.state.toRoute<Router.Connections>()
        provideConnectionsFeature(GcipId.from(data.walletId))
    }

    val state by feature.state.global.observeSafeState()

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.Connections),
                onNavigateUp = { navigator.navigateUp() }
            )
        }
    ) { padding ->
        if (state.connections.isEmpty() && !state.isLoading) {
            Column(Modifier.padding(padding)) {
                AvoirEmptyScreen()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.connections, key = { it.id.fmt }) { connection ->
                    ConnectionCell(
                        connection = connection,
                        onDelete = { feature.sendAction(ConnectionsAction.DeleteConnection(connection.id)) }
                    )
                }
            }
        }
    }
}

