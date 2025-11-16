package foundation.openstore.gcip.core.encryption

import foundation.openstore.gcip.core.transport.GcipId

interface EcdhEncryptionProvider {
    suspend fun generatePair(): KeyPair
    suspend fun generateSharedSecret(eid: GcipId, privateKey: ByteArray, publicKey: ByteArray): ByteArray
}
