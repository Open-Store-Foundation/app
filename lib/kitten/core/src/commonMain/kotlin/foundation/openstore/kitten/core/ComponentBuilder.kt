package org.openwallet.kitten.core

import foundation.openstore.kitten.api.Scope
import foundation.openstore.kitten.api.SingleThread

class ComponentBuilder<Component> {

    private val componentsMultimap = HashMap<Any?, HashMap<Any, Component>>()

    @SingleThread
    fun singleOwner(scope: Scope<out Any>, key: Any? = null, builder: () -> Component): Component {
        val ref = componentsMultimap[key]?.let { componentsMap ->
            val existingComponent = componentsMap.values.firstOrNull()
                ?: return@let null

            existingComponent
        }

        if (ref != null) {
            return ref
        }

        val owner = scope.owner()
        val new = createNew(owner, scope, key, builder)
        registerScope(owner, scope, key)

        return new
    }

    @SingleThread
    fun multiOwner(scope: Scope<out Any>, key: Any? = null, builder: () -> Component): Component {
        val owner = scope.owner()

        val ref = owner?.let ref@ {
            componentsMultimap[key]?.let { componentsMap ->
                val existingComponent = componentsMap.values.firstOrNull()
                    ?: return@ref null

                componentsMap[owner] = existingComponent

                existingComponent
            }
        }

        if (ref != null) {
            return ref
        }

        val new = createNew(owner, scope, key, builder)
        registerScope(owner, scope, key)

        return new
    }

    private fun createNew(owner: Any?, scope: Scope<out Any>, key: Any? = null, builder: () -> Component): Component {
        val newValue = builder.invoke()

        componentsMultimap[key] = HashMap<Any, Component>()
            .apply {
                if (scope.isActive() && owner != null) {
                    set(owner, newValue)
                } else {
                    // TODO log
                }
            }

        return newValue
    }

    private fun registerScope(owner: Any?, scope: Scope<out Any>, key: Any?) {
        scope.register {
            if (owner != null) {
                componentsMultimap[key]?.remove(owner)
            } else {
                // TODO log
            }
        }
    }
}