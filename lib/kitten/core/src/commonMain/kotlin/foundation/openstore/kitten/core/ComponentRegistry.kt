package foundation.openstore.kitten.core

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.LifecycleBorrower
import foundation.openstore.kitten.api.Scope

/**
 * Base class for a module's component registry.
 *
 * This class serves as the main entry point for defining how components within a module are created
 * and scoped. It extends [LifecycleBorrower] to manage lifecycle-aware access to components.
 */
open class ComponentRegistry : LifecycleBorrower() {

    protected val owner: Scope<out Any>
	    get() {
        return innerOwner ?: throw IllegalStateException("Component is not initialized!!")
    }

    protected val provider = ComponentBuilderProducer()

    private var innerOwner: Scope<out Any>? = null

    override fun lock(owner: Scope<out Any>) {
        innerOwner = owner
    }

    override fun unlock(owner: Scope<out Any>) {
        innerOwner = null
    }

    /**
     * Registers a singleton component definition.
     *
     * @param T The component type.
     * @param id The unique identifier for the component (defaults to the class).
     * @param key An optional key for further ensuring uniqueness (e.g. named instances).
     * @param builder The function to create the component.
     * @return A [ComponentHolder] for delegation.
     */
    protected inline fun <reified T : Component> singleton(
        id: Any = T::class,
        key: Any? = null,
        noinline builder: () -> T
    ) : ComponentHolder<T> {
        return ComponentHolder {
            provider.produceBuilder<T>(id)
                .singleton(owner, key, builder)
        }
    }

    /**
     * Registers a shared component definition.
     *
     * Shared components are scoped to the current owner of this registry instance.
     *
     * @param T The component type.
     * @param id The unique identifier for the component.
     * @param key An optional key.
     * @param builder The function to create the component.
     * @return A [ComponentHolder] for delegation.
     */
    protected inline fun <reified T : Component> shared(
        id: Any = T::class,
        key: Any? = null,
        noinline builder: () -> T
    ) : ComponentHolder<T>  {
        return ComponentHolder {
            provider.produceBuilder<T>(id)
                .shared(owner, key, builder)
        }
    }
}
