package org.openwallet.kitten.core

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector
import foundation.openstore.kitten.api.SingleThread

// TODO add destroy for KittenScope
class DependencyRegistry<Provider : ComponentProvider>(
    private val provider: Provider
) {

    init {
        KittenInjector.init(
            {},
            provider
        )
    }

    @SingleThread
    fun <Cmp : Component> create(reg: () -> Cmp) {
        KittenInjector.injectWith(KittenScope) { reg() }
    }

    @SingleThread
    fun <Deps : Any> register(initializer: Injector<Deps>, reg: () -> Deps) {
        initializer.init(
            { reg.invoke() },
            provider
        )
    }
}
