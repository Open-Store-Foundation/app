package foundation.openstore.kitten.viewmodel

import foundation.openstore.kitten.android.withStatelessViewModel
import foundation.openstore.kitten.core.Kitten
import foundation.openstore.kitten.test.core.injector
import foundation.openstore.kitten.viewmodel.utils.TestComponent
import foundation.openstore.kitten.viewmodel.utils.TestComponentRegistry
import foundation.openstore.kitten.viewmodel.utils.viewModelScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ViewModelState {

    @Test
    fun scopedComponent_shouldDiffer_whenDestroyed() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { scoped }
        }

        val dep = viewModelScope { injector.withStatelessViewModel { lazyDep }.also { destroy() } }
        val dep1 = viewModelScope { injector.withStatelessViewModel { lazyDep }.also { destroy() } }

        assertNotEquals(dep.id, dep1.id)
    }

    @Test
    fun scopedComponent_shouldDifferAcrossOwners_whenNotDestroyed() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { scoped }
        }

        val dep = viewModelScope { injector.withStatelessViewModel { lazyDep } }
        val dep1 = viewModelScope { injector.withStatelessViewModel { lazyDep } }

        assertNotEquals(dep.id, dep1.id)
    }

    @Test
    fun singletonComponent_shouldPersist_whenDestroyed() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { singleton }
        }

        val dep = viewModelScope { injector.withStatelessViewModel { lazyDep }.also { destroy() } }
        val dep1 = viewModelScope { injector.withStatelessViewModel { lazyDep }.also { destroy() } }

        assertEquals(dep.id, dep1.id)
    }

    @Test
    fun singletonComponent_shouldBeSameInstance() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { singleton }
        }

        val dep = viewModelScope { injector.withStatelessViewModel { lazyDep } }
        val dep1 = viewModelScope { injector.withStatelessViewModel { lazyDep } }

        assertEquals(dep.id, dep1.id)
    }

    @Test
    fun sharedComponent_shouldPersist_whenScopeNotDestroyed() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { shared }
        }

        val dep = viewModelScope { injector.withStatelessViewModel { lazyDep } }
        val dep1 = viewModelScope { injector.withStatelessViewModel { lazyDep } }

        assertEquals(dep.id, dep1.id)
    }

    @Test
    fun sharedComponent_shouldRecreate_whenDestroyed() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { shared }
        }

        val dep = viewModelScope { injector.withStatelessViewModel { lazyDep }.also { destroy() } }
        val dep1 = viewModelScope { injector.withStatelessViewModel { lazyDep }.also { destroy() } }

        assertNotEquals(dep.id, dep1.id)
    }
}

