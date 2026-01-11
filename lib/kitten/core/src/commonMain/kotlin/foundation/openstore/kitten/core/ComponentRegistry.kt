package foundation.openstore.kitten.core

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.LifecycleBorrower
import foundation.openstore.kitten.api.scope.Scope
import foundation.openstore.kitten.api.scope.KittenScope
import foundation.openstore.kitten.core.utils.KeyGenerator
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.fetchAndIncrement

/**
 * Base class for a module's component registry.
 *
 * This class serves as the main entry point for defining how components within a module are created
 * and scoped. It extends [LifecycleBorrower] to manage lifecycle-aware access to components.
 */
open class ComponentRegistry(
    private val generator: KeyGenerator = { counter.fetchAndIncrement() }
) : LifecycleBorrower() {

    private companion object {
        private val counter = AtomicInt(0)
    }

    /**
     * The current scope owner.
     *
     * This property provides access to the scope currently "borrowed" by the registry.
     * It throws an [IllegalStateException] if the registry is not currently initialized (locked).
     */
    protected val owner: Scope<out Any>
        get() {
            return innerScope ?: throw IllegalStateException("Component is not initialized!!")
        }

    protected val provider = ComponentBuilderProducer()

    private var innerScope: Scope<out Any>? = null

    override fun lock(scope: Scope<out Any>) {
        innerScope = scope
    }

    override fun unlock(scope: Scope<out Any>) {
        innerScope = null
    }

    /**
     * Defines a singleton component in the registry.
     *
     * @param id The unique identifier for the component (defaults to the component's class or key).
     * @param key An optional key to distinguish multiple instances of the same component type.
     * @param scope The scope for the singleton (defaults to [KittenScope]).
     * @param builder A lambda to create the component instance.
     * @return A [ComponentHolder] capable of providing the component.
     */
    protected fun <T : Component> singleton(
        id: Any? = null,
        key: Any? = null,
        scope: Scope<out Any> = KittenScope,
        builder: () -> T
    ) : ComponentHolder<T> {
        return ComponentHolder(key = generator.next()) { holderId ->
            provider.produceBuilder<T>(id ?: holderId)
                .singleton(scope, key, builder)
        }
    }

    /**
     * Defines a shared component in the registry.
     *
     * Shared components are tied to the lifecycle of the current owner.
     *
     * @param id The unique identifier for the component.
     * @param key An optional key to distinguish multiple instances.
     * @param builder A lambda to create the component instance.
     * @return A [ComponentHolder] capable of providing the component.
     */
    protected fun <T : Component> shared(
        id: Any? = null,
        key: Any? = null,
        builder: () -> T
    ) : ComponentHolder<T>  {
        return ComponentHolder(key = generator.next()) { holderId ->
            provider.produceBuilder<T>(id ?: holderId)
                .shared(owner, key, builder)
        }
    }

    /**
     * Defines a scoped component in the registry.
     *
     * Scoped components are resolved within the context of the current scope owner.
     *
     * @param id The unique identifier for the component.
     * @param key An optional key to distinguish multiple instances.
     * @param builder A lambda to create the component instance.
     * @return A [ComponentHolder] capable of providing the component.
     */
    protected fun <T : Component> scoped(
        id: Any? = null,
        key: Any? = null,
        builder: () -> T
    ) : ComponentHolder<T>  {
        return ComponentHolder(key = generator.next()) { holderId ->
            provider.produceBuilder<T>(id ?: holderId)
                .scoped(owner, key, builder)
        }
    }

    /**
     * Directly provides a singleton instance of a component.
     *
     * Use this when you need to obtain the instance immediately rather than a [ComponentHolder].
     *
     * @param key The key to identify the component.
     * @param id The identifier for the component (defaults to the class [T]).
     * @param builder A lambda to create the component instance.
     * @return The singleton component instance.
     */
    protected inline fun <reified T : Component> provideSingleton(
        key: Any,
        id: Any = T::class,
        noinline builder: () -> T
    ) : T {
        return provider.produceBuilder<T>(id)
            .singleton(KittenScope, key, builder)
    }

    /**
     * Directly provides a shared instance of a component.
     *
     * Use this when you need to obtain the instance immediately rather than a [ComponentHolder].
     *
     * @param key The key to identify the component.
     * @param id The identifier for the component (defaults to the class [T]).
     * @param builder A lambda to create the component instance.
     * @return The shared component instance.
     */
    protected inline fun <reified T : Component> provideShared(
        key: Any,
        id: Any = T::class,
        noinline builder: () -> T
    ) : T  {
        return provider.produceBuilder<T>(id)
            .shared(owner, key, builder)
    }

    /**
     * Directly provides a scoped instance of a component.
     *
     * Use this when you need to obtain the instance immediately rather than a [ComponentHolder].
     *
     * @param key The key to identify the component.
     * @param id The identifier for the component (defaults to the class [T]).
     * @param builder A lambda to create the component instance.
     * @return The scoped component instance.
     */
    protected inline fun <reified T : Component> provideScoped(
        key: Any,
        id: Any = T::class,
        noinline builder: () -> T
    ) : T  {
        return provider.produceBuilder<T>(id)
            .scoped(owner, key, builder)
    }
}
