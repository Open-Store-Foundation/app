package foundation.openstore.kitten.viewmodel.utils

import androidx.lifecycle.Lifecycle

fun <T> viewModelScope(
    state: Lifecycle.Event = Lifecycle.Event.ON_CREATE,
    applier: TestOwner.() -> T,
): T {
    return DefaultTestOwner().run {
        event(state)
        applier()
    }
}
