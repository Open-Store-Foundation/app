package com.openstore.core.signing

actual class PlatformCryptography {
    actual object Secp256k1 {
        actual fun pubKey(priv32: ByteArray): ByteArray {
            return fr.acinq.secp256k1.Secp256k1.pubkeyCreate(priv32)
        }

        actual fun sign(
            priv32: ByteArray,
            messageHash32: ByteArray
        ): ByteArray {
            return fr.acinq.secp256k1.Secp256k1.sign(messageHash32, priv32)
        }
    }
}