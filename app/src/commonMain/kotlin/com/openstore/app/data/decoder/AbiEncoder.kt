package com.openstore.app.data.decoder

import com.appmattus.crypto.Algorithm
import com.openstore.app.core.common.remove0x

object AbiEncoder {

    fun encodeGetBuild(versionCode: Long): String {
        val selector = encodeSelector("getBuild(uint256)")
        val data = encodeInt64(versionCode)
        return "$selector$data"
    }

    fun encodeGetGeneralInfo(): String {
        val selector = encodeSelector("getGeneralInfo()")
        return selector
    }

    fun encodeGetDistribution(): String {
        val selector = encodeSelector("getDistribution()")
        return selector
    }

    fun encodeGetOwnershipVersion(app: String, buildId: Long): String {
        val selector = encodeSelector("getOwnershipVersion(address,uint256)")
        val data = encodeAddress(app) + encodeInt64(buildId)
        return selector + data
    }

    fun encodeGetLastObjVersion(app: String, channel: Int): String {
        val selector = encodeSelector("getLastAppVersion(address,uint256)")
        val data = encodeAddress(app) + encodeUint8(channel)
        return selector + data
    }

    fun encodeGetState(version: ULong): String {
        val selector = encodeSelector("getState(uint256)")
        val data = encodeUint64(version)
        return selector + data
    }

    private fun encodeSelector(signature: String): String {
        val digest = Algorithm.Keccak256.createDigest()
        val result = digest.digest(signature.encodeToByteArray())
        val hash = result.toHexString()
        return "0x${hash.substring(0..7)}"
    }

    private fun encodeInt64(value: Long): String {
        val hex = value.toULong().toString(16)
        return pad32(hex)
    }

    private fun encodeUint64(value: ULong): String {
        val hex = value.toString(16)
        return pad32(hex)
    }

    private fun encodeUint8(value: Int): String {
        val hex = value.toString(16)
        return pad32(hex)
    }

    private fun encodeAddress(address: String): String {
        val clean = address.remove0x().lowercase()
        return pad32(clean)
    }

    private fun pad32(hex: String): String = hex.padStart(64, '0')
}
