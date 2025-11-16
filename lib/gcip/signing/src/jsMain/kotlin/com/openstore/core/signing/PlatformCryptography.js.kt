package com.openstore.core.signing

actual class PlatformCryptography {
    actual object Secp256k1 {
        actual fun pubKey(priv32: ByteArray): ByteArray {
            return byteArrayOf(-1) // TODO
        }

        actual fun sign(
            priv32: ByteArray,
            messageHash32: ByteArray
        ): ByteArray {
            return byteArrayOf(-1) // TODO
        }
    }
}