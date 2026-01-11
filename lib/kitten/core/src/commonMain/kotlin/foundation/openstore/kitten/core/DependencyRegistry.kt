package foundation.openstore.kitten.core

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector
import foundation.openstore.kitten.api.SingleThread
import foundation.openstore.kitten.api.scope.KittenScope

/**
 * Manages the initialization and registration of dependencies within the [Kitten] system.
 *
 * This class bridges the [ComponentRegistry] with the internal [KittenInjector], allowing
 * the setup of component graphs and the execution of injection logic.
 *
 * @param Registry The type of the component registry.
 * @param registry The registry instance.
 */
class DependencyRegistry<Registry : ComponentRegistry>(
    private val registry: Registry
) {

    init {
        KittenInjector.init(
            {},
            registry
        )
    }

    /**
     * Eagerly creates a component using the provided provider function.
     *
     * This is typically used in the application initialization block to ensure root components
     * are created immediately.
     *
     * @param Cmp The type of the component.
     * @param provider The function to create the component.
     */
    @SingleThread
    fun <Cmp : Component> create(provider: Registry.() -> Cmp) {
        KittenInjector.injectWith(KittenScope) { provider(registry) }
    }

    /**
     * Registers an external injector to rely on this registry for providing dependencies.
     *
     * This connects a module's injector object to the central registry.
     *
     * @param Cmp The type of the component.
     * @param injector The injector to register.
     * @param provider A function that retrieves the component instance from the registry.
     */
    @SingleThread
    fun <Cmp : Component> register(injector: Injector<Cmp>, provider: Registry.() -> Cmp) {
        injector.init(
            delegate = { provider.invoke(registry) },
            borrower = registry
        )
    }
}
