package foundation.openstore.kitten.android.scopes

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Runnable
import foundation.openstore.kitten.api.scope.Scope

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

    /**
     * Checks if the scope is currently active.
     *
     * @return `true` if the [LifecycleOwner]'s state is at least [Lifecycle.State.CREATED], `false` otherwise.
     */
    override fun isActive(): Boolean {
        return owner.lifecycle.currentState > Lifecycle.State.DESTROYED
    }

    /**
     * returns the [LifecycleOwner] associated with this scope.
     */
    override fun owner(): Any {
        return owner
    }

    /**
     * Registers a destructor to be executed when the scope is destroyed.
     *
     * The destructor will be called when the [LifecycleOwner] reaches the [Lifecycle.State.DESTROYED] state.
     * If the scope is already inactive, the destructor is not registered and `false` is returned.
     *
     * @param destructor The [Runnable] to execute during cleanup.
     * @return `true` if the destructor was successfully registered, `false` otherwise.
     */
    override fun register(destructor: Runnable): Boolean {
        if (!isActive()) {
            return false
        }

        owner.lifecycle.addObserver(Destructor(destructor))
        return true
    }

    private class Destructor(val destructor: Runnable) : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            destructor.run()
        }
    }
}
