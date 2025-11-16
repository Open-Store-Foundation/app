# Trust Wallet MVI

## MVI Components

**`MviFeature`** - the main MVI unit, inherit `ViewModel`, responsible for

- execution `MviAction`
- updating `MviState`,
- mapping `MviState` -> `MviViewState`

**`MviFeatureDelegate`** - utility delegate class, responsible for

- execution specific `MviAction`
- updating `MviState`

**`MviAction`** - utility entity to notify `MviFeature` about some `View` events

**`MviRelay`** - coroutine `MutableSharedFlow`, responsible for notification `View` about some one-way events
from `MviFeature`

**`MviState`** - current data screen state, could be single `data class` on several `sealead class`, could have some
data we don't need on `View` layer

**`MviViewState`** - current view screen state, the set of `MviProperty`, marked as `@Stable`

**`MviProperty`** - property for state observation in `Composable` function

## Example

``` kotlin
// 1 - Create FooFeature, inherit it from MviFrature and put some dependecies to the contructor
@HiltViewModel
class FooFeature @Inject constructor(
  private val fooRepo: FooRepo
) : MviFeature()
```

``` kotlin
// 2 - Inside FooFeature create MviAction, MviState, MviViewState and set inital state
@HiltViewModel
class FooFeature @Inject constructor(
  private val fooRepo: FooRepo
) : MviFeatureMviFeature<FooFeature.Action, FooFeature.State, FooFeature.ViewState>(
    initialState = State.Loading
) {

    sealed interface Action : MviAction {
        data class Init(val listId: String) : Action
    }

    sealed interface State : MviState {
        object Loading : State
        data class Error(val error: Throwable) : State
        data class Data(val users: List<Foo>) : State
    }

    class ViewState(
        val isLoading: MviProperty<Boolean>,
        val error: MviProperty<Throwable?>,
        val title: MviProperty<String>,
        val foos: MviProperty<List<Foo>>
    ) : MviViewState
}
```

``` kotlin
// 3 - Inside FooFeature implement mapping State -> ViewState and Action execution functions
class FooFeature {
  // ... class defenision

      private val remote = RemoveDelegate(this, fooRepo)

      override fun createViewState(): ViewState {
        return buildViewState { // this: MviBinder.Builder
            ViewState(
                // you can call mviProperty only inside buildViewState function
                mviProperty { it is State.Loading },
                mviProperty { onState<State.Error>()?.error },
                mviProperty { if (it is State.Loading) "Loading..." else "Foo List" },
                mviProperty { onState<State.Data>()?.users.orEmpty() },
            )
        }
    }

    override suspend fun executeAction(action: Action) {
        when (action) {
            // You can handle Action in-place
            is Action.Init -> bgScope.launch {
                try {
                    val users = userRepo.getUsers()
                    setState { State.Data(users) } // update state
                } catch (e: Exception) {
                    setState { State.Error(e) } // update state
                }
            }
            // Or delegate it to MviFeatureDelegate
            is Action.DeleteItem -> remote.excute(action)
        }
    }

  // ... Action, State, ViewState
}

class RemoveDelegate(
    feature: FooFeature,
    private val fooRepo: FooRepo,
) : MviFeatureDelegate<FooFeature.Action.DeleteItem, FooFeature.State>(
    feature = feature
) {

    override suspend fun executeAction(action: NodeStatusFeature.Action.AddNode) {
        bgScope.launch { // has access to scopes
                try {
                    userRepo.deleteUser(action.user)
                    setState<State.Data> { // set state only if current state is State.Data
                        copy(users = users.filter { it.id != action.user.id })
                    }
                } catch (e: Exception) {
                    setState { State.Error(e) } // update state
                }
            }
        }
    }
}
```

``` kotlin
// 4 - Create composable and observe the ViewState
@Composable
fun Content() {
  val viewModel = hiltViewModel<FooFeature>()

  OnLifecycleEvent { _, e ->
      if (e != Lifecycle.Event.ON_CREATE) {
          return@OnLifecycleEvent
      }

      viewModel.sendAction(FooFeature.Init(SOME_ID))
  }


  // Observe 2 states in place
  val isLoading by viewModel.state.isLoading.observeSafeState()
  val error by viewModel.state.error.observeSafeState()

  when {
      isLoading -> Loading()
      error != null -> Error(throwable = error)
      // Pass ViewState to the next Composable function
      else -> Data(viewModel.state) {
        viewModel.sendAction(FooFeature.Action.DeleteItem(it))
      }
  }
}
```

## Scheme

<img width="725" alt="image" src="https://user-images.githubusercontent.com/15245196/214261849-cbec00d5-20a4-4dc4-826e-08843d916706.png">

