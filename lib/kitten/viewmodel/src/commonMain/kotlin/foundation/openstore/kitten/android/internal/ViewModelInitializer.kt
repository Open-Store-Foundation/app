package foundation.openstore.kitten.android.internal

import androidx.lifecycle.ViewModel

/**
 * Internal helper to intercept ViewModel creation and attach lifecycle observers.
 *
 * This class facilitates the connection between the creation of a ViewModel and the
 * registration of lifecycle callbacks (e.g., [AutoCloseable]) within the [Scope].
 *
 * @param Delegate The type of the component delegate.
 * @param Subject The type of the ViewModel.
 * @param factory A function that creates the ViewModel instance.
 */
class ViewModelInitializer<Delegate : Any, Subject : ViewModel>(
    private val factory: (Delegate) -> Subject
) {

    private var observer: ((Subject) -> Unit)? = null

    fun create(delegate: Delegate): Subject {
        val subject = factory(delegate)

        observer?.invoke(subject)

        return subject
    }

    fun observe(o: (Subject) -> Unit) {
        observer = o
    }
}
