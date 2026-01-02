package foundation.openstore.gcip.core.handler.internal

import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.transport.CoseKey
import foundation.openstore.gcip.core.transport.GcipEncryptionMessage
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray

internal sealed interface EncryptionMessageParams {
    val encryption: Encryption
    class Plain(override val encryption: Encryption.Handshake.Request) : EncryptionMessageParams
    class Encrypted(override val encryption: Encryption.Replier, val iv: ByteArray) : EncryptionMessageParams
}

internal fun GcipEncryptionMessage.getEncryptionMessageType(): GcipResult<EncryptionMessageParams> {
    val eid = eid
    val iv = iv
    val exchangeKey = exchangeKey?.let { Cbor.CoseCompliant.decodeFromByteArray<CoseKey>(it) }

    if (eid != null && iv != null && exchangeKey != null) {
        val key = exchangeKey.toExchangeKey()
            ?: return GcipResult.err(GcipStatus.InvalidFormat)

        return GcipResult.ok(
            EncryptionMessageParams.Encrypted(
                encryption = Encryption.Handshake.Response(
                    key = key,
                    eid = eid,
                ),
                iv = iv,
            )
        )
    }

    if (eid == null && iv == null && exchangeKey != null) {
        val key = exchangeKey.toExchangeKey()
            ?: return GcipResult.err(GcipStatus.InvalidFormat)

        return GcipResult.ok(
            EncryptionMessageParams.Plain(
                encryption = Encryption.Handshake.Request(key),
            )
        )
    }

    if (eid != null && iv != null && exchangeKey == null) {
        return GcipResult.ok(
            EncryptionMessageParams.Encrypted(
                encryption = Encryption.Session(eid),
                iv = iv,
            )
        )
    }

    return GcipResult.err(GcipStatus.InvalidFormat)
}
