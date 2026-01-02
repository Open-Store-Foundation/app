package foundation.openstore.gcip.core.data

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.Int

interface GcipNonceProvider {
    fun generate(): Short
}

class GcipNonceProviderAtomic(
    initialNonce: Int = 0,
) : GcipNonceProvider {

    companion object Companion {
        const val MAX_NONCE = Short.MAX_VALUE.toInt()
    }

    private val counter = AtomicInt(initialNonce)

    override fun generate(): Short {
        return (counter.fetchAndIncrement() % MAX_NONCE)
            .toShort()

    }
}
