package foundation.openstore.kitten.test.core

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.deps.depLazy
import foundation.openstore.kitten.api.scope.KittenScope
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestDep(
    val id: Int = Random.nextInt(),
)

class TestComponent : Component {
    val dep: TestDep = TestDep()
    val lazyDep: TestDep by depLazy { TestDep() }
}

class TestComponentRegistry : ComponentRegistry() {
    val singleton by singleton { TestComponent() }
    val shared by shared { TestComponent() }
    val scoped by scoped { TestComponent() }

    fun test(data: Int): TestComponent {
        return provideScoped(data) {
            TestComponent()
        }
    }
}

class LifecycleTest {

    @Test
    fun singletonComponent_shouldBeSameInstance() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { singleton }
        }

        val dep1 = injector.injectWith(KittenScope) { dep }
        val lazyDep1 = injector.injectWith(KittenScope) { lazyDep }

        val dep2 = injector.injectWith(KittenScope) { dep }
        val lazyDep2 = injector.injectWith(KittenScope) { lazyDep }

        assertEquals(dep1.id, dep2.id)
        assertEquals(lazyDep1.id, lazyDep2.id)
    }

    @Test
    fun sharedComponent_shouldDifferFromSingleton() {
        val shared = injector<TestComponent>()
        val singleton = injector<TestComponent>()

        Kitten.init(TestComponentRegistry()) {
            register(shared) { this.shared }
            register(singleton) { this.singleton }
        }

        val lazyDep1 = testScope { shared.injectWith(it) { lazyDep } }
        val lazyDep2 = singleton.injectWith(KittenScope) { lazyDep }

        assertNotEquals(lazyDep1.id, lazyDep2.id)
    }

    @Test
    fun sharedComponent_shouldDifferAcrossScopes() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { shared }
        }

        val lazyDep1 = testScope { injector.injectWith(it) { lazyDep } }
        val lazyDep2 = testScope { injector.injectWith(it) { lazyDep } }

        assertNotEquals(lazyDep1.id, lazyDep2.id)
    }

    @Test
    fun sharedComponent_shouldBeSameWithinScope() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { shared }
        }

        testScope {
            val lazyDep1 = injector.injectWith(it) { lazyDep }
            val lazyDep2 = injector.injectWith(it) { lazyDep }
            assertEquals(lazyDep1.id, lazyDep2.id)
        }
    }

    @Test
    fun scopedComponent_shouldDifferAcrossScopes() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { scoped }
        }

        val lazyDep1 = testScope { injector.injectWith(it) { lazyDep } }
        val lazyDep2 = testScope { injector.injectWith(it) { lazyDep } }

        assertNotEquals(lazyDep1.id, lazyDep2.id)
    }

    @Test
    fun sharedComponent_shouldPersist_whenScopeNotDestroyed() {
        val injector = injector<TestComponent>()
        Kitten.init(TestComponentRegistry()) {
            register(injector) { shared }
        }

        val lazyDep1 = testScope(destroy = false) { injector.injectWith(it) { lazyDep } }
        val lazyDep2 = testScope(destroy = false) { injector.injectWith(it) { lazyDep } }

        assertEquals(lazyDep1.id, lazyDep2.id)
    }
}
