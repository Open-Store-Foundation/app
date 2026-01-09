package foundation.openstore.kitten.android.scopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import foundation.openstore.kitten.android.internal.ViewModelInitializer
import foundation.openstore.kitten.api.Scope
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
    private var vm: Subject? = null
    private var closable: AutoCloseable? = null

    init {
        init.observe { viewModel ->
            val safeClosable = closable
            if (safeClosable != null) {
                viewModel.addCloseable(safeClosable)
                closable = null
            } else {
                vm = viewModel
            }
        }
    }

    override fun isActive(): Boolean {
        return isActive
    }

    override fun owner(): Any? {
        return owner?.viewModelStore
    }

    override fun register(provider: Runnable): Boolean {
        val safeVm = vm
        val closable = object : AutoCloseable {
            override fun close() {
                provider.run()
                isActive = false
                owner = null
            }
        }

        if (safeVm != null) {
            safeVm.addCloseable(closable)
            vm = null
        } else {
            this.closable = closable
        }

        return true
    }
}
