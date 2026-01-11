package foundation.openstore.kitten.android.scopes

import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import foundation.openstore.kitten.android.internal.ViewModelInitializer
import foundation.openstore.kitten.api.scope.Scope
import kotlinx.coroutines.Runnable

/**
 * A scope tied to an Android [ViewModel].
 *
 * This scope manages the lifecycle of dependencies injected into a ViewModel.
 * It uses the [ViewModel.addCloseable] API to ensure cleanup occurs when the ViewModel is cleared.
 *
 * @param Delegate The type of the component delegate.
 * @param Subject The type of the ViewModel.
 * @param owner The [ViewModelStoreOwner] associated with the ViewModel.
 * @param init The [ViewModelInitializer] used to intercept creation and setup observation.
 */
class ViewModelScope<Delegate : Any, Subject : ViewModel>(
    private var owner: ViewModelStoreOwner? = null,
    init: ViewModelInitializer<Delegate, Subject>
) : Scope<ViewModel> {

    private var isActive = true
    private var destructors: MutableList<Runnable> = ArrayList(2)

    init {
        init.observe { viewModel ->
            viewModel.addCloseable(
                object : AutoCloseable {
                    override fun close() {
                        isActive = false
                        destructors.fastForEach { it.run() }
                        owner = null
                    }
                }
            )
        }
    }

    /**
     * Checks if the ViewModel scope is currently active.
     *
     * @return `true` if the ViewModel has not been cleared yet, `false` otherwise.
     */
    override fun isActive(): Boolean {
        return isActive
    }

    /**
     * Returns the [androidx.lifecycle.ViewModelStore] associated with this scope.
     *
     * @throws IllegalStateException if the scope is not active or the owner is destroyed.
     */
    override fun owner(): Any {
        if (!isActive) {
            throw IllegalStateException("Scope is not active")
        }

        val store = owner?.viewModelStore
            ?: throw IllegalStateException("Scope owner is already destroyed")

        return store
    }

    /**
     * Registers a destructor to be executed when the ViewModel is cleared.
     *
     * @param destructor The [Runnable] to execute during cleanup.
     * @return `true` if the destructor was successfully registered, `false` otherwise.
     */
    override fun register(destructor: Runnable): Boolean {
        if (!isActive) {
            return false
        }

        this.destructors.add(destructor)
        return true
    }
}
