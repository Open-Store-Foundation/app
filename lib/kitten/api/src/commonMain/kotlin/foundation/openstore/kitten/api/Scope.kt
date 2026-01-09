package foundation.openstore.kitten.api

import kotlinx.coroutines.Runnable

/**
 * Represents the scope or lifecycle of a component owner.
 *
 * A Scope determines how long a dependency should live and when it should be cleaned up.
 * It is typically tied to the lifecycle of an object (e.g., an Activity, Fragment, or a custom class).
 *
 * @param T The type of the owner object.
 */
interface Scope<T> {
    /**
     * Checks if the scope is currently active.
     *
     * @return `true` if the scope is valid and active, `false` otherwise.
     */
    fun isActive(): Boolean

    /**
     * Retrieves the owner object associated with this scope.
     *
     * @return The owner object, or `null` if the reference has been cleared or is not available.
     */
    fun owner() : Any?

    /**
     * Registers a callback to be executed when the scope is destroyed or invalid.
     *
     * @param provider The [Runnable] to execute upon scope destruction.
     * @return `true` if the callback was successfully registered, `false` if the scope is already inactive.
     */
    fun register(provider: Runnable): Boolean
}
