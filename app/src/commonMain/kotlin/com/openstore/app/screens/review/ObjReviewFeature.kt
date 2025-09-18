package com.openstore.app.screens.review


import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty

sealed interface ReviewAction : MviAction {

}

data class ReviewState(
    val name: String
) : MviState

class ReviewViewState(
    val name: MviProperty<String>
) : MviViewState

class ObjReviewFeature(

) : MviFeature<ReviewAction, ReviewState, ReviewViewState>(initState = ReviewState("")) {

    override fun createViewState(): ReviewViewState {
        return buildViewState {
            ReviewViewState(mviProperty { it.name })
        }
    }

    override suspend fun executeAction(action: ReviewAction) {

    }
}