package foundation.openstore.gcip.core.data

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.Int

interface GcipNonceProvider {
    fun generate(): UShort
}

class GcipNonceProviderAtomic(
    initialNonce: Int = 0,
) : GcipNonceProvider {
    private val counter = AtomicInt(initialNonce)

    override fun generate(): UShort {
        return counter.fetchAndIncrement()
            .toUShort()
    }
}
