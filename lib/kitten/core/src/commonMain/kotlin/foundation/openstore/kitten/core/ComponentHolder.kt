package foundation.openstore.kitten.core

import foundation.openstore.kitten.api.Component

/**
 * A holder that delegates the retrieval of a component instance.
 *
 * This class is used in conjunction with property delegation to provide the component instance
 * when accessed.
 *
 * @param T The type of the component.
 * @param initializer The function that provides the component instance (e.g., from a builder).
 */
class ComponentHolder<out T : Component>(
    internal val key: Any,
    internal val initializer: (Any) -> T
) {
    /**
     * Delegate operator to retrieve the component instance.
     */
    operator fun getValue(thisRef: Any?, property: Any?) : T {
        return initializer(key)
    }
}