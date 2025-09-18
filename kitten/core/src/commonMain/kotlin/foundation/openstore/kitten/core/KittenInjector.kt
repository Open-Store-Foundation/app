package org.openwallet.kitten.core

import kotlinx.coroutines.Runnable
import foundation.openstore.kitten.api.Injector
import foundation.openstore.kitten.api.Scope

internal object KittenScope : Scope<Unit> {
    override fun isActive(): Boolean {
        return true
    }

    override fun owner(): Unit {
        return Unit
    }

    override fun register(provider: Runnable): Boolean {
        return true
    }
}

internal object KittenInjector : Injector<Unit>()
