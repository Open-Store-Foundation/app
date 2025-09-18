package foundation.openstore.kitten.android

import foundation.openstore.kitten.api.Injector

inline fun <Delegate : Any, reified Subject> Injector<Delegate>.withSingleton(
    noinline creator: Delegate.() -> Subject
): Subject {
    val subject = injectWith(SingletonScope) {
        creator(this)
    }

    return subject
}
