package foundation.openstore.kitten.viewmodel.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore

class DefaultTestOwner : TestOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()
    override val lifecycle = LifecycleRegistry(this)

    init {
        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    viewModelStore.clear()
                }
            }
        )
    }

    override fun event(event: Lifecycle.Event) {
        lifecycle.handleLifecycleEvent(event)
    }
}
