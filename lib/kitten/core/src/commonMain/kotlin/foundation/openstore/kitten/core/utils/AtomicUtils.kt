package foundation.openstore.kitten.core.utils

import kotlin.concurrent.atomics.AtomicInt

internal fun AtomicInt.fetchAndIncrement(): Int {
    return fetchAndAdd(1)
}
