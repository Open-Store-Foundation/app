package foundation.openstore.kitten.test.core

import foundation.openstore.kitten.core.scope.OwnedScope
import kotlin.random.Random

fun <T> testScope(
    create: Boolean = true,
    destroy: Boolean = true,
    action: (OwnedScope) -> T
): T {
    val testScope = OwnedScope(Random.nextInt())

    if (create) {
        testScope.create()
    }

    val result = action(testScope)

    if (destroy) {
        testScope.destroy()
    }

    return result
}
