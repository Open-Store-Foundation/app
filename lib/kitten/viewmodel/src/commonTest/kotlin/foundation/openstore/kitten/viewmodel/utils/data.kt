package foundation.openstore.kitten.viewmodel.utils

import androidx.lifecycle.ViewModel
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.deps.depLazy
import foundation.openstore.kitten.core.ComponentRegistry
import kotlin.random.Random

class TestDep(
    val id: Int = Random.nextInt(),
) : ViewModel()

class TestComponent : Component {
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