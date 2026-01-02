package foundation.openstore.gcip.core.encryption

import foundation.openstore.gcip.core.ExchangeKey
import foundation.openstore.gcip.core.transport.CoseCurve

class KeyPair(
    val pk: ByteArray,
    val pub: ByteArray, // sometimes belongs to PK, sometimes to peer
    val algo: CoseCurve.Exchange = CoseCurve.Exchange.P256
) : AutoCloseable {

    fun getExchangeKey(): ExchangeKey {
        return ExchangeKey(pub, algo)
    }

    override fun close() {
        pk.fill(0)
        pub.fill(0)
    }
}
