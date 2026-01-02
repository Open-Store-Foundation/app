package com.openstore.app.screens.queue

import com.openstore.app.data.installation.InstallationRequestRepo
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty

sealed interface QueueAction : MviAction {
    object Refresh : QueueAction
}

data class QueueState(
    val name: String
) : MviState

class QueueViewState(
    val name: MviProperty<String>
) : MviViewState

class QueueFeature(
   private val installationRepo: InstallationRequestRepo
) : MviFeature<QueueAction, QueueState, QueueViewState>(
    initState = QueueState(""),
    initAction = QueueAction.Refresh
) {

    override fun createViewState(): QueueViewState {
        return buildViewState {
            QueueViewState(mviProperty { it.name })
        }
    }

    override suspend fun executeAction(action: QueueAction) {

    }
}