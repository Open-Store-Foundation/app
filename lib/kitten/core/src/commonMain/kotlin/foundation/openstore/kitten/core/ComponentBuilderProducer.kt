package foundation.openstore.kitten.core

import foundation.openstore.kitten.api.Component

/**
 * Checks and creates [ComponentBuilder]s for specific component types.
 *
 * This class ensures that there is only one builder responsible for creating instances
 * of a specific component type (identified by class or key).
 */
class ComponentBuilderProducer {

	private val builders = HashMap<Any, ComponentBuilder<out Component>>()

	/**
	 * Retrieves or creates a [ComponentBuilder] for the given class/key.
	 *
	 * @param T The type of the component.
	 * @param clazz The key or class identifier for the component.
	 * @return The [ComponentBuilder] responsible for managing instances of [T].
	 */
	@Suppress("UNCHECKED_CAST")
	fun <T : Component> produceBuilder(clazz: Any) : ComponentBuilder<T> {
		val component = builders[clazz] as? ComponentBuilder<T>

		if (component != null) {
			return component
		}

		val componentBuilder = ComponentBuilder<T>()
		builders[clazz] = componentBuilder
		return componentBuilder
	}
}
