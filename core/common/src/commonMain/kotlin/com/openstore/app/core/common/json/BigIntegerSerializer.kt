package com.openstore.app.json

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.openstore.app.core.common.HexPrefix
import com.openstore.app.core.common.remove0x
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object BigIntegerSerializer : KSerializer<BigInteger> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigInteger {
        val value = decoder.decodeString()
        return when { // TODO v2
            value.isEmpty() || value == HexPrefix -> BigInteger.ZERO
            value.startsWith(HexPrefix) -> value.remove0x().toBigInteger(16)
            else -> value.toBigInteger()
        }
    }

    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeString(value.toString())
    }
}