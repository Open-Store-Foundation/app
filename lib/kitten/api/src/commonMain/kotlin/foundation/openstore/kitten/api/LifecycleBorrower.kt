package foundation.openstore.kitten.api

import foundation.openstore.kitten.api.scope.Scope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
/**
 * Abstract base class for managing the lifecycle and thread safety of dependency access.
 *
 * Implements a mechanism to "borrow" resources (dependencies) securely, ensuring that
 * the component or dependency is locked for use and then unlocked, handling concurrency via synchronization.
 */
abstract class LifecycleBorrower {

    private val lock = SynchronizedObject()

    /**
     * Executes a block of code with acquired access to the managed resources.
     *
     * This method ensures thread safety by synchronizing on an internal lock object.
     * It locks the owner scope before execution and unlocks it afterwards.
     *
     * @param Subject The return type of the factory block.
     * @param scope The scope owner requesting access.
     * @param factory The block of code to execute.
     * @return The result of the factory block.
     */
    @SingleThread
    internal fun <Subject> borrow(
        scope: Scope<out Any>,
        factory: () -> Subject
    ): Subject {
        synchronized(lock) {
            lock(scope)
            val obj = factory()
            unlock(scope)

            return obj
        }
    }

    /**
     * Locks the resources associated with the given owner.
     *
     * @param scope The scope owner to lock.
     */
    @SingleThread
    protected abstract fun lock(scope: Scope<out Any>)

    /**
     * Unlocks the resources associated with the given owner.
     *
     * @param scope The scope owner to unlock.
     */
    @SingleThread
    protected abstract fun unlock(scope: Scope<out Any>)
}
