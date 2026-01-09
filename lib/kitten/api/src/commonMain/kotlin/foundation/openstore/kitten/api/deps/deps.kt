package foundation.openstore.kitten.api.deps

import foundation.openstore.kitten.api.SingleThread

//@SingleThread
//@ExperimentalNativeApi
//fun <Dep : Any> depRc(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepRc(initializer)

/**
 * Creates a lazily initialized dependency.
 *
 * The dependency is created only when it is first accessed and cached for subsequent uses.
 * This is similar to Kotlin's [lazy] delegate but tailored for the Kitten library's [LazyDep] mechanism.
 *
 * @param Dep The type of the dependency.
 * @param initializer The function that creates the dependency instance.
 * @return A [LazyDep] instance that delegates the creation and caching of the dependency.
 */
@SingleThread
fun <Dep : Any> depLazy(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepGod(initializer)
