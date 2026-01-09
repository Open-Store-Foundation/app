package foundation.openstore.kitten.core

import foundation.openstore.kitten.api.Injector

/**
 * Internal injector used by [DependencyRegistry] and [Kitten] to perform internal dependency operations.
 *
 * This object is not exposed to the public API and serves as a bridge for the library internals.
 */
internal object KittenInjector : Injector<Unit>()
