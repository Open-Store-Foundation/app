package foundation.openstore.kitten.android

import androidx.lifecycle.LifecycleOwner
import foundation.openstore.kitten.api.Injector
import foundation.openstore.kitten.android.LifecycleScope

context(LifecycleOwner)
inline fun <Delegate : Any, reified Subject> Injector<Delegate>.withLifecycle(
    noinline creator: Delegate.() -> Subject
): Subject {
    val owner = this@LifecycleOwner

    val subject = injectWith(LifecycleScope(owner)) {
        creator(this)
    }

    return subject
}
