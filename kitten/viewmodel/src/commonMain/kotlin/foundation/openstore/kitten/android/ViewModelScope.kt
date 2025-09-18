package foundation.openstore.kitten.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.Runnable
import foundation.openstore.kitten.api.Scope

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
