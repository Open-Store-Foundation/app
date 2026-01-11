package foundation.openstore.kitten.viewmodel.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner

interface TestOwner : ViewModelStoreOwner, LifecycleOwner {
    fun event(event: Lifecycle.Event)

    fun create() = event(Lifecycle.Event.ON_CREATE)
    fun start() = event(Lifecycle.Event.ON_START)
    fun resume() = event(Lifecycle.Event.ON_RESUME)
    fun pause() = event(Lifecycle.Event.ON_PAUSE)
    fun stop() = event(Lifecycle.Event.ON_STOP)
    fun destroy() = event(Lifecycle.Event.ON_DESTROY)
}