package com.openstore.core.signing

class ExtendedPrivateKey(
    val pk: ByteArray,
    val chain: ByteArray,
) {
    companion object Companion {
        fun from(data: ByteArray): ExtendedPrivateKey {
            if (data.size < 64) {
                throw IllegalStateException("Invalid extended key")
            }

            val pk = data.copyOfRange(0, 32)
            val chain = data.copyOfRange(32, 64)

            return ExtendedPrivateKey(pk, chain)
        }
    }
}
