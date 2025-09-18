package foundation.openstore.kitten.api

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
abstract class LifecycleBorrower {

    private val lock = SynchronizedObject()

    @SingleThread
    internal fun <Subject> borrow(
        owner: Scope<out Any>,
        factory: () -> Subject
    ): Subject {
        synchronized(lock) {
            lock(owner)
            val obj = factory()
            unlock(owner)

            return obj
        }
    }

    @SingleThread
    protected abstract fun lock(owner: Scope<out Any>)

    @SingleThread
    protected abstract fun unlock(owner: Scope<out Any>)
}
