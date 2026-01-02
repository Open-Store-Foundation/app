package foundation.openstore.gcip.encryption

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.EC
import dev.whyoleg.cryptography.algorithms.ECDH
import foundation.openstore.gcip.core.encryption.EcdhEncryptionProvider
import foundation.openstore.gcip.core.encryption.KeyPair
import foundation.openstore.gcip.core.transport.GcipId

class EcdhProviderDefault : EcdhEncryptionProvider {

    private val provider = CryptographyProvider.Default

    override suspend fun generatePair(): KeyPair {
        val keyPair = provider.get(ECDH)
            .keyPairGenerator(EC.Curve.P256)
            .generateKey()

        val pubBytes = keyPair.publicKey.encodeToByteArray(EC.PublicKey.Format.RAW)
        val pk = keyPair.privateKey.encodeToByteArray(EC.PrivateKey.Format.RAW)

        return KeyPair(
            pub = pubBytes,
            pk = pk
        )
    }

    override suspend fun generateSharedSecret(eid: GcipId, privateKey: ByteArray, publicKey: ByteArray): ByteArray {
        val pk = provider.get(ECDH)
            .privateKeyDecoder(EC.Curve.P256)
            .decodeFromByteArray(
                EC.PrivateKey.Format.RAW, privateKey
            )

        val pubKey = provider.get(ECDH)
            .publicKeyDecoder(EC.Curve.P256)
            .decodeFromByteArray(
                EC.PublicKey.Format.RAW, publicKey
            )

        val sharedSecret = pk.sharedSecretGenerator()
            .generateSharedSecretToByteArray(pubKey)

        return sharedSecret
    }
}
