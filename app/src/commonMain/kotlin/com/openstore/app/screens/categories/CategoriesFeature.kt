package com.openstore.app.screens.categories

import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviSubject
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.Router
import com.openstore.app.data.CategoryId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

sealed interface CategoriesAction : MviAction {
    data class Query(val query: String) : CategoriesAction
}

data class CategoriesState(
    val categories: List<CategoryId>?
) : MviState

class CategoriesViewState(
    val categories: MviProperty<List<CategoryId>?>
) : MviViewState

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CategoriesFeature(
    data: Router.Categories
) : MviFeature<CategoriesAction, CategoriesState, CategoriesViewState>(
    initState = CategoriesState(null)
) {

    private val consumer = MviSubject<String>()
    private var categories = emptyList<CategoryId>()

    init {
        consumer.events
            .debounce(400L)
            .distinctUntilChanged()
            .mapLatest { query ->
                val result = categories.filter { it.name.contains(query, ignoreCase = true) }

                setState {
                    copy(categories = result)
                }
            }
            .launchIn(bgScope)

        bgScope.launch {
            val result = CategoryId.byType(data.typeId)

            setState {
                this@CategoriesFeature.categories = result
                copy(categories = result)
            }
        }
    }

    override fun createViewState(): CategoriesViewState {
        return buildViewState {
            CategoriesViewState(mviProperty { it.categories })
        }
    }

    override suspend fun executeAction(action: CategoriesAction) {
        when (action) {
            is CategoriesAction.Query -> {
                consumer.emit(action.query)
            }
        }
    }
}