package foundation.openstore.kitten.android

import androidx.lifecycle.LifecycleOwner
import foundation.openstore.kitten.android.scopes.LifecycleScope
import foundation.openstore.kitten.api.scope.KittenScope
import foundation.openstore.kitten.api.Injector

/**
 * Injects a dependency with a lifecycle scoped to the [LifecycleOwner].
 *
 * The dependency is created using a [LifecycleScope], ensuring it remains active
 * as long as the [LifecycleOwner] is not destroyed.
 *
 * @param Delegate The type of the component or delegate managed by the injector.
 * @param Subject The type of the dependency to create.
 * @param owner The [LifecycleOwner] (e.g., Activity or Fragment) that defines the scope.
 * @param creator A function to create the dependency using the delegate.
 * @return The created dependency.
 */
context(owner: LifecycleOwner)
inline fun <Delegate : Any, reified Subject> Injector<Delegate>.withLifecycle(
    noinline creator: Delegate.() -> Subject
): Subject {
    val subject = injectWith(LifecycleScope(owner)) {
        creator(this)
    }

    return subject
}

/**
 * Injects a dependency with a global singleton scope.
 *
 * The dependency is created using [KittenScope] and will live for the application's lifetime.
 *
 * @param Delegate The type of the component or delegate managed by the injector.
 * @param Subject The type of the dependency to create.
 * @param creator A function to create the dependency using the delegate.
 * @return The created dependency.
 */
inline fun <Delegate : Any, reified Subject> Injector<Delegate>.withSingleton(
    noinline creator: Delegate.() -> Subject
): Subject {
    val subject = injectWith(KittenScope) {
        creator(this)
    }

    return subject
}
