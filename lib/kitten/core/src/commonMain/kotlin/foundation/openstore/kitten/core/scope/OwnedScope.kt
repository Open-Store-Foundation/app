package foundation.openstore.kitten.core.scope

import foundation.openstore.kitten.api.scope.Scope
import kotlinx.coroutines.Runnable

/**
 * A scope that is manually obtained and managed by a specific owner object.
 *
 * This scope allows manual control over its lifecycle via the [create] and [destroy] methods.
 * Unlike automatic scopes tied to system components (like Activity or ViewModel), an OwnedScope
 * is designed to be tied to any arbitrary object ("owner") and requires explicit activation and deactivation.
 *
 * @property owner The object that owns this scope.
 * @property isActive The initial active state of the scope.
 */
class OwnedScope(
    private val owner: Any,
    private var isActive: Boolean = false
) : Scope<Any> {

    private var destructors: MutableList<Runnable> = mutableListOf()

    /**
     * Activates the scope.
     *
     * Calling this method allows components and destructors to be registered within the scope.
     */
    fun create() {
        isActive = true
    }

    /**
     * Destroys the scope.
     *
     * This marks the scope as inactive and executes all registered destructors.
     * After calling this, the list of destructors is cleared.
     */
    fun destroy() {
        isActive = false

        destructors.forEach { it.run() }
        destructors.clear()
    }

    /**
     * Checks if the scope is manually marked as active.
     */
    override fun isActive(): Boolean {
        return isActive
    }

    /**
     * Returns the owner object associated with this scope.
     */
    override fun owner(): Any {
        return owner
    }

    /**
     * Registers a destructor to be executed when [destroy] is called.
     *
     * @param destructor The [Runnable] to execute during cleanup.
     * @return `true` if the scope is active and the destructor was registered, `false` otherwise.
     */
    override fun register(destructor: Runnable): Boolean {
        if (!isActive) {
            return false
        }

        destructors.add(destructor)
        return true
    }
}