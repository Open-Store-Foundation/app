package foundation.openstore.kitten.core

import foundation.openstore.kitten.api.Scope
import foundation.openstore.kitten.api.SingleThread

/**
 * Manages the creation and retrieval of components within a specific scope.
 *
 * This class implements the logic for caching components (Singleton vs Shared) and
 * associating them with their owners.
 *
 * @param Component The type of the component being managed.
 */
class ComponentBuilder<Component> {

    private val componentsMultimap = HashMap<Any?, HashMap<Any, Component>>()

    /**
     * Retrieves or creates a singleton instance of a component.
     *
     * A singleton component is created once per key and persists as long as the scope is valid.
     * It is not tied to a specific owner instance but rather to the key (or class if key is null).
     *
     * @param scope The scope in which to register the component.
     * @param key An optional key to distinguish multiple instances of the same component type.
     * @param builder A function to create the component if it doesn't exist.
     * @return The singleton component instance.
     */
    @SingleThread
    fun singleton(scope: Scope<out Any>, key: Any? = null, builder: () -> Component): Component {
        val ref = findComponent(key)

        if (ref != null) {
            return ref
        }

        val owner = scope.owner()
        val new = createNew(owner, scope, key, builder)
        registerScope(owner, scope, key)

        return new
    }

    /**
     * Retrieves or creates a shared instance of a component.
     *
     * A shared component is tied to a specific owner instance (from the scope).
     * Different owners will get different instances of the component, even if the key is the same.
     *
     * @param scope The scope providing the owner.
     * @param key An optional key to distinguish component types.
     * @param builder A function to create the component if it doesn't exist for this owner.
     * @return The shared component instance.
     */
    @SingleThread
    fun shared(scope: Scope<out Any>, key: Any? = null, builder: () -> Component): Component {
        val owner = scope.owner()

        val ref = owner?.let {
            findComponent(key) { componentsMap, existingComponent ->
                componentsMap[owner] = existingComponent
            }
        }

        if (ref != null) {
            return ref
        }

        val new = createNew(owner, scope, key, builder)
        registerScope(owner, scope, key)

        return new
    }

    private fun findComponent(key: Any? = null, apply: ((HashMap<Any, Component>, Component) -> Unit)? = null): Component? {
        return componentsMultimap[key]?.let { componentsMap ->
            val existingComponent = componentsMap.values.firstOrNull()
                ?: return@let null

            apply?.invoke(componentsMap, existingComponent)

            existingComponent
        }
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