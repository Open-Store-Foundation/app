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
import foundation.openstore.kitten.android.internal.ViewModelInitializer
import foundation.openstore.kitten.android.scopes.ViewModelScope
import foundation.openstore.kitten.api.Injector

/**
 * Wrapper for parameters commonly used when creating a ViewModel.
 *
 * @property state The [SavedStateHandle] for the ViewModel.
 * @property extras The [CreationExtras] provided by the ViewModelProvider.
 */
data class ViewModelPrams(
	val state: SavedStateHandle,
	val extras: CreationExtras,
)

/**
 * Creates and injects a stateless ViewModel (one that checks [CreationExtras] but likely doesn't use [SavedStateHandle]).
 *
 * This function handles the boilerplate of creating a [ViewModelProvider.Factory], initializing the
 * [ViewModelScope], and injecting the ViewModel instance.
 *
 * @param Delegate The type of the component delegate.
 * @param Subject The type of the ViewModel.
 * @param key An optional key to identify the ViewModel in the store.
 * @param owner The [ViewModelStoreOwner], defaults to the current LocalViewModelStoreOwner in Compose.
 * @param factory A function to create the ViewModel, receiving [CreationExtras].
 * @return The created or retrieved ViewModel instance.
 */
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

/**
 * Creates and injects a ViewModel within a Composable.
 *
 * This function sets up a [SavedStateHandle] and passes it via [ViewModelPrams].
 *
 * @param Delegate The type of the component delegate.
 * @param Subject The type of the ViewModel.
 * @param key An optional key to identify the ViewModel in the store.
 * @param owner The [ViewModelStoreOwner], defaults to the current LocalViewModelStoreOwner.
 * @param factory A function to create the ViewModel, receiving [ViewModelPrams].
 * @return The created or retrieved ViewModel instance.
 */
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

/**
 * Creates and injects a ViewModel from a non-Composable context (e.g., Fragment or Activity).
 *
 * @param Delegate The type of the component delegate.
 * @param Subject The type of the ViewModel.
 * @param owner The [ViewModelStoreOwner] (implicit receiver).
 * @param key An optional key for the ViewModel.
 * @param creator A function to create the ViewModel, receiving [ViewModelPrams].
 * @return The created or retrieved ViewModel instance.
 */
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

/**
 * Helper to retrieve the current [ViewModelStoreOwner] in Compose.
 *
 * @throws IllegalStateException if no [ViewModelStoreOwner] is found (e.g., not properly set up).
 */
@Composable
fun requireVmStoreOwner(): ViewModelStoreOwner {
	return checkNotNull(LocalViewModelStoreOwner.current) {
		"No LifecycleOwner was provided via LocalLifecycleOwner"
	}
}
