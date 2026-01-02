package org.openwallet.kitten.core

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.LifecycleBorrower
import foundation.openstore.kitten.api.Scope

open class ComponentProvider : LifecycleBorrower() {

    protected val owner: Scope<out Any>
	    get() {
        return innerOwner ?: throw IllegalStateException("Component is not initialized!!")
    }

    protected val provider = ComponentProviderProducer()

    private var innerOwner: Scope<out Any>? = null

    override fun lock(owner: Scope<out Any>) {
        innerOwner = owner
    }

    override fun unlock(owner: Scope<out Any>) {
        innerOwner = null
    }

    protected inline fun <reified T : Component> singleOwner(
        id: Any = T::class,
        key: Any? = null,
        noinline builder: () -> T
    ) : T  {
        return provider.produceBuilder<T>(id)
            .singleOwner(owner, key, builder)
    }

    protected inline fun <reified T : Component> multiOwner(
        id: Any = T::class,
        key: Any? = null,
        noinline builder: () -> T
    ) : T  {
        return provider.produceBuilder<T>(id)
            .multiOwner(owner, key, builder)
    }
}
