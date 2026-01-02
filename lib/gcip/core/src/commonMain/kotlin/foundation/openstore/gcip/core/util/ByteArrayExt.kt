package foundation.openstore.gcip.core.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal inline fun <T> ByteArray.use(block: (ByteArray) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        block(this)
    } finally {
        this.fill(0)
    }
}
