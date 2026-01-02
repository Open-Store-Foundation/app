package foundation.openstore.signer.app.screens.create

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty

sealed interface CreateWalletAction : MviAction {
    
}

data class CreateWalletState(
    val isLoading: Boolean = false
) : MviState

class CreateWalletViewState(
    val isLoading: MviProperty<Boolean>
) : MviViewState

class CreateWalletFeature : MviFeature<CreateWalletAction, CreateWalletState, CreateWalletViewState>(
    initState = CreateWalletState()
) {
    override fun createViewState(): CreateWalletViewState {
        return buildViewState {
            CreateWalletViewState(mviProperty { it.isLoading })
        }
    }

    override suspend fun executeAction(action: CreateWalletAction) {
        
    }
}

