package com.openstore.app.screens.home


import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty

sealed interface HomeAction : MviAction {

}

data class HomeState(
    val name: String
) : MviState

class HomeViewState(
    val name: MviProperty<String>
) : MviViewState

class HomeFeature(

) : MviFeature<HomeAction, HomeState, HomeViewState>(initState = HomeState("")) {

    override fun createViewState(): HomeViewState {
        return buildViewState {
            HomeViewState(mviProperty { it.name })
        }
    }

    override suspend fun executeAction(action: HomeAction) {

    }
}