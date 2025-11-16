package com.openstore.app.features.catalog.screens.home

import com.openstore.app.features.catalog.data.CatalogRepo
import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty

sealed interface Action : MviAction {

}

data class State(
    val name: String
) : MviState

class ViewState(
    val name: MviProperty<String>
) : MviViewState

class CatalogHomeFeature(
    private val catalog: CatalogRepo
) : MviFeature<Action, State, ViewState>(initState = State("")) {

    override fun createViewState(): ViewState {
        return buildViewState {
            ViewState(mviProperty { it.name })
        }
    }

    override suspend fun executeAction(action: Action) {
    }
}
