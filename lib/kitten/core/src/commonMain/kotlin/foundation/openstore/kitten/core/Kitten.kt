package foundation.openstore.kitten.core

/**
 * Entry point for the Kitten Dependency Injection library.
 *
 * This singleton object holds the root configuration and manages the initialization process.
 */
object Kitten {

    private var registry: DependencyRegistry<*>? = null

    /**
     * Initializes the Kitten library with a component registry.
     *
     * This method should be called once, typically in the Application's onCreate method or equivalent entry point.
     *
     * @param Provider The type of the component registry.
     * @param registry The instance of the component registry (typically the root/app registry).
     * @param applier A configuration block to setup the dependency graph (e.g., creating components, registering module injectors).
     */
    fun <Provider : ComponentRegistry> init(
        registry: Provider,
        applier: DependencyRegistry<Provider>.(Provider) -> Unit,
    ) {
        if (Kitten.registry != null) {
            return
        }

        Kitten.registry = DependencyRegistry(registry = registry)
            .apply {
                applier(registry)
            }
    }
}
