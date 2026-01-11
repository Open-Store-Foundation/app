package foundation.openstore.kitten.test.core

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector

fun <C : Component> injector(): Injector<C> {
    return object : Injector<C>() {}
}
