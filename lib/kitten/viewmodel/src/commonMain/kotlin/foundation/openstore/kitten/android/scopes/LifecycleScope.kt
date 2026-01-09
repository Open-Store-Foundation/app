package foundation.openstore.kitten.android.scopes

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Runnable
import foundation.openstore.kitten.api.Scope

/**
 * A scope tied to an Android [LifecycleOwner].
 *
 * This scope is active as long as the [LifecycleOwner] is not in the [Lifecycle.State.DESTROYED] state.
 * It registers a [DefaultLifecycleObserver] to trigger cleanup when the owner is destroyed.
 *
 * @param owner The [LifecycleOwner] (e.g., Activity or Fragment) to which this scope is attached.
 */
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
