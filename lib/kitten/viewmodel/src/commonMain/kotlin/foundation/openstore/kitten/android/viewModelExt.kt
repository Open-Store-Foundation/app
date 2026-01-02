package foundation.openstore.kitten.android

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import foundation.openstore.kitten.api.Injector

data class ViewModelPrams(
	val state: SavedStateHandle,
	val extras: CreationExtras,
)

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

// TODO remove and combine with withViewModel
@Composable
inline fun <Delegate : Any, reified Subject : ViewModel> Injector<Delegate>.withStatelessViewModel(
    key: String? = null,
    owner: ViewModelStoreOwner = requireVmStoreOwner(),
    noinline factory: Delegate.(CreationExtras) -> Subject
): Subject {
    return viewModel(
        viewModelStoreOwner = owner,
        key = key,
        initializer = initializer@ {
            val init = ViewModelInitializer<Delegate, Subject> {
                factory(it, this@initializer)
            }

            injectWith(ViewModelScope(owner, init)) {
                init.create(this)
            }
        }
    )
}

@Composable
inline fun <Delegate : Any, reified Subject : ViewModel> Injector<Delegate>.withViewModel(
	key: String? = null,
	owner: ViewModelStoreOwner = requireVmStoreOwner(),
	noinline factory: Delegate.(ViewModelPrams) -> Subject
): Subject {
	return viewModel(
		viewModelStoreOwner = owner,
		key = key,
		initializer = initializer@ {
			val savedStateHandle = createSavedStateHandle()

			val init = ViewModelInitializer<Delegate, Subject> {
				factory(it, ViewModelPrams(savedStateHandle, this@initializer))
			}

			injectWith(ViewModelScope(owner, init)) {
				init.create(this)
			}
		}
	)
}

context(owner: ViewModelStoreOwner)
inline fun <Delegate : Any, reified Subject : ViewModel> Injector<Delegate>.withViewModel(
	key: String? = null,
	noinline creator: Delegate.(ViewModelPrams) -> Subject
): Subject {
	val clazz = Subject::class

	val factory = viewModelFactory {
		initializer {
			val savedStateHandle = createSavedStateHandle()

			val init = ViewModelInitializer<Delegate, Subject> {
				creator(it, ViewModelPrams(savedStateHandle, this@initializer))
			}

			injectWith(ViewModelScope(owner, init)) {
				init.create(this)
			}
		}
	}

	val extras = if (owner is HasDefaultViewModelProviderFactory) {
        owner.defaultViewModelCreationExtras
	} else {
		CreationExtras.Empty
	}

	val provider = ViewModelProvider.create(owner, factory, extras)


	if (key != null) {
		return provider[key, clazz]
	}

	return provider[clazz]
}

@Composable
fun requireVmStoreOwner(): ViewModelStoreOwner {
	return checkNotNull(LocalViewModelStoreOwner.current) {
		"No LifecycleOwner was provided via LocalLifecycleOwner"
	}
}
