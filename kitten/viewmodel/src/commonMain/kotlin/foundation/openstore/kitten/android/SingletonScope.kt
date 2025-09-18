package foundation.openstore.kitten.android

import kotlinx.coroutines.Runnable
import foundation.openstore.kitten.api.Scope

object SingletonScope : Scope<Unit> {
    override fun isActive(): Boolean {
        return true
    }

    override fun owner(): Unit? {
        return Unit
    }

    override fun register(provider: Runnable): Boolean {
        return true
    }
}
