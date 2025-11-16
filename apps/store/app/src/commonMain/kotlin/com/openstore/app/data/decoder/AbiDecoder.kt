package com.openstore.app.data.decoder

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.openstore.app.core.common.remove0x
import com.openstore.app.core.common.toLower0xHex
import com.openstore.app.core.common.toUpper0xHex

data class AppDistributionPluginV1Data(
    val typeId: Int,
    val sources: List<String>
)

data class AppOwnerPluginV1Version(
    val domain: String,
    val fingerprints: List<ByteArray>,
    val blockNumber: Long
)

data class AppGeneralInfo(
    val packageStr: String,
    val name: String,
    val description: String,
    val protocolId: Int,
    val platformId: Int,
    val categoryId: Int,
)

data class AppBuild(
    val referenceId: String,
    val protocolId: Int,
    val versionName: String,
    val versionCode: Long,
    val checksum: String,
)

data class AppOwnershipProofsInfo(
    val version: Long,
    val certs: List<ByteArray>,
    val proofs: List<ByteArray>
)

data class AppAndOwnershipVersion(
    val app: Long,
    val ownership: Long,
)

data class AssetOwnershipStatus(
    val status: Long,
    val lastSuccessDelta: Long,
)

object AbiDecoder {

    fun decodeSingleLong(hexString: String): Long {
        val rawBytes = hexString.remove0x()
            .hexToByteArray()

        val value = readUint256(rawBytes, 0).longValue()

        return value
    }

    fun decodeDoubleLong(hexString: String): Pair<Long, Long> {
        val rawBytes = hexString.remove0x()
            .hexToByteArray()

        val first = readUint256(rawBytes, 0).longValue()
        val second = readUint256(rawBytes, 32).longValue()

        return first to second
    }

    fun decodeAssetOwnershipStatus(hexString: String): AssetOwnershipStatus {
        val (status, lastSuccessDelta) = decodeDoubleLong(hexString)
        return AssetOwnershipStatus(status, lastSuccessDelta)
    }

    fun decodeAppAndOwnershipVersion(hexString: String): AppAndOwnershipVersion {
        val (versionCode, ownershipVersion) = decodeDoubleLong(hexString)
        return AppAndOwnershipVersion(versionCode, ownershipVersion)
    }

    /**
     * Decodes the raw hex string for the AppGeneralInfo struct.
     *
     * struct AppGeneralInfo {
     *     string id;          // dynamic
     *     string name;        // dynamic
     *     string description; // dynamic
     *     uint16 platformId;  // static
     *     uint16 categoryId;  // static
     *     uint16 platformId;  // static
     * }
     *
     * 0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000120000000000000000000000000000000000000000000000000000000000000016000000000000000000000000000000000000000000000000000000000000001a000000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000003968747470733a2f2f7261772e67697468756275736572636f6e74656e742e636f6d2f416e6472657743687570696e2f45766d4163636f756e7400000000000000000000000000000000000000000000000000000000000000000000000000001d6f72672e6f70656e73746f72652e6578616d706c652e616e64726f696400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010536f6d65206465736372697074696f6e00000000000000000000000000000000
     */
    fun decodeGeneralInfo(hexString: String): AppGeneralInfo {
        val rawBytes = hexString.remove0x()
            .hexToByteArray()

        // The first 32 bytes of a function result point to the start of the returned data.
        // For a single struct return, this is usually 32.
        val structStartOffset = readUint256(rawBytes, 0).intValue()

        // --- Head of the struct ---
        // Pointers to dynamic types
        val packagePtrLoc = structStartOffset + 0
        val namePtrLoc = packagePtrLoc + 32
        val descriptionPtrLoc = namePtrLoc + 32

        // Static types are packed in the next slot
        val staticDataSlotOffset = descriptionPtrLoc + 32

        // --- Decode static part ---
        // uint16s are 2 bytes each, right-aligned in the 32-byte word.
        // `platform_id` is last, so it's at the very end.
        val protocolId = readUint16(rawBytes, staticDataSlotOffset)

        val platformId = readUint16(rawBytes, staticDataSlotOffset + 32)

        // `category_id` comes just before it.
        val categoryId = readUint16(rawBytes, staticDataSlotOffset + 64)

        // --- Decode dynamic parts (strings) ---
        // The offsets read from pointers are relative to the start of the struct's data area.
        val packageStr = decodeDynamicString(rawBytes, packagePtrLoc, structStartOffset)
        val name = decodeDynamicString(rawBytes, namePtrLoc, structStartOffset)
        val description = decodeDynamicString(rawBytes, descriptionPtrLoc, structStartOffset)

        return AppGeneralInfo(
            packageStr = packageStr,
            name = name,
            description = description,
            protocolId = protocolId,
            platformId = platformId,
            categoryId = categoryId,
        )
    }

    /**
     *
     * Decodes the raw hex string for the AppDistributionPluginV1Data struct.
     * This version is more efficient and avoids creating multiple buffers.
     *
     * struct AppDistributionPluginV1Data {
     *     uint16 typeId;
     *     bytes[] sources;
     * }
     * 0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000008b68747470733a2f2f676e66642d746573746e65742d7370322e626e62636861696e2e6f72672f766965772f6b697474796465762f6f70656e2d73746f72652d65787465726e616c2f6f72675f6f70656e73746f72655f6578616d706c655f616e64726f69642f762f247b56455253494f4e5f4e414d457d2f247b56455253494f4e5f434f44457d2e61706b000000000000000000000000000000000000000000
     *
     * @param hexString The raw hex string from the RPC call, starting with "0x".
     * @return A parsed [AppDistributionPluginV1Data] object.
     * @throws IllegalArgumentException if the data is malformed.
     */
    fun decodeDistributionSources(hexString: String): AppDistributionPluginV1Data {
        val rawBytes = hexString.remove0x()
            .hexToByteArray()

        // The first 32 bytes point to the start of the struct's data.
        val structStartOffset = readUint256(rawBytes, 0).intValue()

        // --- Decode static part of the struct ---
        // The typeId (uint16) is in the first slot, right-aligned.
        val typeIdSlotLocation = structStartOffset
        val typeId = readUint16(rawBytes, typeIdSlotLocation)

        // --- Decode dynamic part of the struct ---
        // The pointer to the 'sources' array is in the second slot.
        val sourcesArrayPointerLocation = structStartOffset + 32

        // Use the new helper to decode the entire string array in one call.
        val sources = decodeStringArray(rawBytes, sourcesArrayPointerLocation, structStartOffset)

        return AppDistributionPluginV1Data(typeId, sources)
    }

    /**
     * Decodes the raw hex string for the AppOwnerPluginV1Version struct.
     *
     * struct AppOwnerPluginV1Version {
     *     string domain;
     *     bytes32[] fingerprints;
     *     uint256 blockNumber;
     * }
     *
     */
    fun decodeOwnershipInfo(hexString: String): AppOwnerPluginV1Version {
        val rawBytes = hexString.remove0x()
            .hexToByteArray()

        val structStartOffset = readUint256(rawBytes, 0).intValue()

        // --- Head of the struct ---
        val domainPtrLoc = structStartOffset + 0
        val fingerprintsPtrLoc = structStartOffset + 32
        val blockNumberSlotLoc = structStartOffset + 64

        // --- Decode dynamic parts ---
        val domain = decodeDynamicString(rawBytes, domainPtrLoc, structStartOffset)

        // --- Decode `bytes32[] fingerprints` (dynamic array of static type) ---
        val fingerprints = decodeBytes32Array(rawBytes, fingerprintsPtrLoc, structStartOffset)
        val blockNumber = readUint256(rawBytes, blockNumberSlotLoc).longValue()

        return AppOwnerPluginV1Version(
            domain = domain,
            fingerprints = fingerprints,
            blockNumber = blockNumber
        )
    }
    /**
     * Decodes log topics and data for
     * AppOwnerChanged(uint256 indexed version, bytes[] certs, bytes[] proofs).
     *
     * topics[1] holds the indexed `version` (topics[0] is the event signature).
     * The data blob encodes `certs` and `proofs` in standard ABI-encoded format.
     *
     * @param topics Log topics array.
     * @param dataHex Log data hex (0x-prefixed) for certs and proofs.
     * @return Parsed event fields as [AppOwnershipProofsInfo].
     * @throws IllegalArgumentException if topics/data are malformed.
     */
    fun decodeAppOwnerChanged(topics: List<String>, dataHex: String): AppOwnershipProofsInfo {
        val versionTopic = topics.getOrNull(1)
            ?: throw IllegalArgumentException("Missing indexed version topic")
        val version = readUint256(versionTopic.remove0x().hexToByteArray(), 0).longValue()

        val dataBytes = dataHex.remove0x().hexToByteArray()
        val certs = decodeBytesArray(dataBytes, 0, 0)
        val proofs = decodeBytesArray(dataBytes, 32, 0)

        return AppOwnershipProofsInfo(
            version = version,
            certs = certs,
            proofs = proofs
        )
    }

    /**
     * Decodes the raw hex string for the getBuild function result.
     *
     * struct AppBuild {
     *     bytes referenceId;   // dynamic
     *     uint16 protocolId;   // static
     *     string versionName;  // dynamic
     *     uint256 versionCode; // static
     *     bytes32 checksum;    // static
     * }
     *
     * 0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000001973b20000000000000000000000000000000000000000000000000000000000000005312e302e30000000000000000000000000000000000000000000000000000000312e302e30000000000000000000000000000000000000000000000000000000
     */
    fun decodeAppBuild(hexString: String): AppBuild {
        val rawBytes = hexString.remove0x()
            .hexToByteArray()

        val structStartOffset = readUint256(rawBytes, 0).intValue()

        val referenceIdPtrLoc = structStartOffset + 0
        val protocolIdSlotLoc = structStartOffset + 32
        val versionNamePtrLoc = structStartOffset + 64
        val versionCodeSlotLoc = structStartOffset + 96
        val checksumSlotLoc = structStartOffset + 128

        val referenceId = decodeDynamicBytes(rawBytes, referenceIdPtrLoc, structStartOffset).toUpper0xHex()
        val protocolId = readUint16(rawBytes, protocolIdSlotLoc)
        val versionName = decodeDynamicString(rawBytes, versionNamePtrLoc, structStartOffset)
        val versionCode = readUint256(rawBytes, versionCodeSlotLoc).longValue()
        val checksum = readBytes32(rawBytes, checksumSlotLoc).toLower0xHex()

        return AppBuild(
            referenceId = referenceId,
            protocolId = protocolId,
            versionName = versionName,
            versionCode = versionCode,
            checksum = checksum,
        )
    }

    private fun decodeBytes32Array(rawBytes: ByteArray, pointerLocation: Int, dataAreaStart: Int): List<ByteArray> {
        // 1. Find the start of the array data (length + elements).
        val relativeOffset = readUint256(rawBytes, pointerLocation).intValue()
        val arrayDataStart = dataAreaStart + relativeOffset

        // 2. Read the length of the array.
        val length = readUint256(rawBytes, arrayDataStart).intValue()
        if (length == 0) return emptyList()

        // 3. Read the elements, which are stored sequentially.
        val elementsStart = arrayDataStart + 32
        return (0 until length).map { i ->
            val elementOffset = elementsStart + (i * 32)
            rawBytes.copyOfRange(elementOffset, elementOffset + 32)
        }
    }

    private fun decodeBytesArray(rawBytes: ByteArray, pointerLocation: Int, dataAreaStart: Int): List<ByteArray> {
        // This is the most complex case: a dynamic array of a dynamic type.
        // 1. Find the start of the array's "header" (length + pointers to elements).
        val arrayHeaderRelativeOffset = readUint256(rawBytes, pointerLocation).intValue()
        val arrayHeaderStart = dataAreaStart + arrayHeaderRelativeOffset

        // 2. Read the array length.
        val length = readUint256(rawBytes, arrayHeaderStart).intValue()
        if (length == 0) return emptyList()

        // 3. The pointers to each `bytes` element start after the length.
        val elementPointersStart = arrayHeaderStart + 32

        return (0 until length).map { i ->
            // 4. For each element, read its own relative offset.
            val elementPointerLocation = elementPointersStart + (i * 32)
            val elementRelativeOffset = readUint256(rawBytes, elementPointerLocation).intValue()

            // 5. Calculate the absolute start of the element's data (its length).
            // This offset is relative to the start of the array's data area.
            val elementLengthLocation = elementPointersStart + elementRelativeOffset

            // 6. Read the length of this specific `bytes` element.
            val bytesLength = readUint256(rawBytes, elementLengthLocation).intValue()
            if (bytesLength == 0) {
                return@map ByteArray(0)
            }

            // 7. Read the content.
            val bytesContentLocation = elementLengthLocation + 32
            rawBytes.copyOfRange(bytesContentLocation, bytesContentLocation + bytesLength)
        }
    }

    private fun decodeDynamicBytes(rawBytes: ByteArray, pointerLocation: Int, dataAreaStart: Int): ByteArray {
        val relativeOffset = readUint256(rawBytes, pointerLocation).intValue()
        val lengthLocation = dataAreaStart + relativeOffset
        val length = readUint256(rawBytes, lengthLocation).intValue()
        if (length == 0) return ByteArray(0)
        val contentLocation = lengthLocation + 32
        return rawBytes.copyOfRange(contentLocation, contentLocation + length)
    }

    private fun readInt32(bytes: ByteArray, offset: Int): Int {
        return (bytes[offset + 28].toInt() shl 24) or
            ((bytes[offset + 29].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 30].toInt() and 0xFF) shl 8) or
            (bytes[offset + 31].toInt() and 0xFF)
    }

    private fun readInt64(bytes: ByteArray, offset: Int): Long {
        var result = 0L
        for (i in 24..31) {
            result = (result shl 8) or (bytes[offset + i].toLong() and 0xFF)
        }
        return result
    }

    /**
     * Reads a 32-byte unsigned integer from a byte array at a given offset.
     */
    private fun readUint256(bytes: ByteArray, offset: Int): BigInteger {
        val word = bytes.copyOfRange(offset, offset + 32) // TODO v3 move to slices
        return BigInteger.fromByteArray(word, Sign.POSITIVE)
    }

    /**
     * Reads a 16-bit unsigned short that is right-aligned in a 32-byte word.
     */
    private fun readUint16(bytes: ByteArray, offset: Int): Int {
        val highByte = bytes[offset + 30].toInt() and 0xFF
        val lowByte = bytes[offset + 31].toInt() and 0xFF
        return (highByte shl 8) or lowByte
    }

    private fun readBytes32(bytes: ByteArray, offset: Int): ByteArray {
        return bytes.copyOfRange(offset, offset + 32)
    }

    /**
     * Reads a single dynamic string from the data buffer.
     * @param rawBytes The complete data buffer.
     * @param pointerLocation The offset where the pointer to the string is located.
     * @param dataAreaStart The absolute start offset of the struct's data area.
     */
    private fun decodeDynamicString(rawBytes: ByteArray, pointerLocation: Int, dataAreaStart: Int): String {
        // 1. Read the relative offset to the string data.
        val relativeOffset = readUint256(rawBytes, pointerLocation).intValue()

        // 2. Calculate the absolute position of the string's length.
        val stringLengthLocation = dataAreaStart + relativeOffset

        // 3. Read the string's length.
        val length = readUint256(rawBytes, stringLengthLocation).intValue()
        if (length == 0) return ""

        // 4. Read the string content.
        val stringContentLocation = stringLengthLocation + 32
        val stringBytes = rawBytes.copyOfRange(stringContentLocation, stringContentLocation + length)

        return stringBytes.decodeToString()
    }

    private fun decodeStringArray(rawBytes: ByteArray, pointerLocation: Int, dataAreaStart: Int): List<String> {
        val arrayHeaderRelativeOffset = readUint256(rawBytes, pointerLocation).intValue()
        val arrayHeaderStart = dataAreaStart + arrayHeaderRelativeOffset

        val length = readUint256(rawBytes, arrayHeaderStart).intValue()
        if (length == 0) return emptyList()

        val elementPointersStart = arrayHeaderStart + 32

        return (0 until length).map { i ->
            val elementPointerLocation = elementPointersStart + (i * 32)
            // Reuse the single string decoder for each element in the array
            decodeDynamicString(rawBytes, elementPointerLocation, elementPointersStart)
        }
    }
}