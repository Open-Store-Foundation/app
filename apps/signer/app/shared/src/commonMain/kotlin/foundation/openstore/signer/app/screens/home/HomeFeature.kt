package foundation.openstore.signer.app.screens.home

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.signer.app.data.settings.WalletSettingsRepository
import foundation.openstore.signer.app.data.wallet.WalletInteractor

sealed interface HomeAction : MviAction

sealed interface HomeEvent

data class HomeState(
    val isLoading: Boolean = false
) : MviState

class HomeViewState(
    val isLoading: MviProperty<Boolean>
) : MviViewState

class HomeFeature : MviFeature<HomeAction, HomeState, HomeViewState>(
    initState = HomeState(),
) {

    private val relay = MviRelay<HomeEvent>()
    val channel = relay.events

    override fun createViewState(): HomeViewState {
        return buildViewState {
            HomeViewState(mviProperty { it.isLoading })
        }
    }

    override suspend fun executeAction(action: HomeAction) {

    }
}
