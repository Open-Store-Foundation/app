package foundation.openstore.kitten.test.graph

import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestComponent : Component

class TestComponentRegistry : ComponentRegistry() {
    val singleton by singleton { TestComponent() }
    val shared by shared { TestComponent() }
    val scoped by scoped { TestComponent() }
}

object SingletonInjector : Injector<TestComponent>()
object SharedInjector : Injector<TestComponent>()
object ScopedInjector : Injector<TestComponent>()

class GraphTest {

    @Test
    fun testFalseGraph() {
        Kitten.init(registry = TestComponentRegistry(), throwOnReinit = false) {
            register(SingletonInjector) { singleton }
        }

        checkGraph("NOT_INIT") { isInit, skipped ->
            assertFalse(isInit, "All graphs are initialized!")
            assertTrue(skipped.contains(ScopedInjector))
            assertTrue(skipped.contains(SharedInjector))
        }
    }

    @Test
    fun testTrueGraph() {
        Kitten.init(registry = TestComponentRegistry(), throwOnReinit = false) {
            register(ScopedInjector) { scoped }
            register(SingletonInjector) { singleton }
            register(SharedInjector) { shared }
        }

        checkGraph("INIT") { isInit, skipped ->
            assertTrue(
                isInit,
                "Some injectors aren't init yet: ${skipped.map { it::class.qualifiedName }}"
            )
        }
    }
}
