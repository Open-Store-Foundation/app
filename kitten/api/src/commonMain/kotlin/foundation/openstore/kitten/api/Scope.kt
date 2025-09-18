package foundation.openstore.kitten.api

import kotlinx.coroutines.Runnable

interface Scope<T> {
    fun isActive(): Boolean
    fun owner() : Any?
    fun register(provider: Runnable): Boolean
}
