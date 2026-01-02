package com.openstore.core.signing

expect class PlatformCryptography {
    object Secp256k1 {
        fun pubKey(priv32: ByteArray): ByteArray
        fun sign(priv32: ByteArray, messageHash32: ByteArray): ByteArray
    }
}
