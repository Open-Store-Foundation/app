package foundation.openstore.kitten.api.scope

import kotlinx.coroutines.Runnable

/**
 * A global scope representing a singleton lifecycle.
 *
 * This scope is always active and never destroyed. It is used for dependencies that should live
 * for the entire duration of the application.
 */
object KittenScope : Scope<Unit> {
    override fun isActive(): Boolean {
        return true
    }

    @Suppress("RedundantUnitReturnType")
    override fun owner(): Unit {
        return
    }

    override fun register(destructor: Runnable): Boolean {
        return true
    }
}
