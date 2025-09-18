package foundation.openstore.kitten.android

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Runnable
import foundation.openstore.kitten.api.Scope

class LifecycleScope(
    private val owner: LifecycleOwner
) : Scope<LifecycleOwner> {

    override fun isActive(): Boolean {
        return owner.lifecycle.currentState > Lifecycle.State.DESTROYED
    }

    override fun owner(): Any {
        return owner
    }

    override fun register(provider: Runnable): Boolean {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return false
        }

        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                provider.run()
            }
        })

        return true
    }
}
