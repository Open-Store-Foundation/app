package foundation.openstore.signer.app.data.mnemonic

import com.openstore.app.core.common.fillZeros
import com.openstore.app.core.common.lazyUnsafe
import com.openstore.app.core.common.toByteArray
import com.openstore.app.core.common.toCharArray
import com.openstore.app.core.common.use
import com.openstore.core.signing.Cryptography
import kotlin.random.Random

enum class EntropySize(val bytes: Int, val words: Int) {
    B128(16, 12), // 12
    B160(20, 15), // 15
    B192(24, 18), // 18
    B224(28, 21), // 21
    B256(32, 24); // 24

    val checksumLengthBits by lazyUnsafe { bytes * 8 / 32 }

    companion object {
        fun from(count: Int): EntropySize? {
            return entries.firstOrNull { it.words == count }
        }
    }
}

class Mnemonic(
    val value: CharArray
) : AutoCloseable {

    companion object {
        fun from(bytes: ByteArray): Mnemonic {
            return Mnemonic(bytes.toCharArray())
        }
    }

    fun unwrap(): List<String> {
        return value.concatToString()
            .split(" ")
    }

    fun bytes(): ByteArray {
        return value.toByteArray()
    }

    override fun close() {
        value.fillZeros()
    }
}

interface MnemonicRepository {
    suspend fun create(size: EntropySize = EntropySize.B128): Mnemonic
    suspend fun seed(mnemonic: Mnemonic, passphrase: ByteArray? = null): ByteArray
    suspend fun validate(mnemonic: String): Mnemonic?
}

class LocalMnemonicRepository(
    private val repo: MnemonicWordsRepository
) : MnemonicRepository {
    
    // Using kotlin.random.Random for now as placeholder for SecureRandom in Common
    // Ideally this should be platform specific SecureRandom
    private val random = Random

    override suspend fun seed(mnemonic: Mnemonic, passphrase: ByteArray?): ByteArray {
        val passPrefix = "mnemonic".encodeToByteArray()

        val salt = when (passphrase) {
            null -> passPrefix
            else -> passPrefix + passphrase
        }

        return Cryptography.Hash.pbkdf2WithHmacSHA512(
            mnemonic = mnemonic.value,
            salt = salt
        )
    }

    override suspend fun create(size: EntropySize): Mnemonic {
        val chars = ByteArray(size.bytes).use { entropy ->
            random.nextBytes(entropy)
            fromEntropy(size, entropy)
        }

        return Mnemonic(chars)
    }

    override suspend fun validate(mnemonic: String): Mnemonic? {
        val validWords = repo.getWords()
        val length = mnemonic.length
        var start = 0
        var wordCount = 0

        while (start < length) {
            while (start < length && mnemonic[start].isWhitespace()) {
                start++
            }

            if (start == length) {
                break
            }

            var end = start
            while (end < length && !mnemonic[end].isWhitespace()) {
                end++
            }

            val wordLen = end - start
            val isValid = validWords.any {
                it.length == wordLen
                        && it.regionMatches(0, mnemonic, start, wordLen, ignoreCase = true)
            }

            if (!isValid) {
                return null
            }
            wordCount++

            start = end
        }

        if (wordCount == 0) {
            return null
        }

        if (EntropySize.from(wordCount) == null) {
            return null
        }

        return Mnemonic(mnemonic.toCharArray())
    }

    private suspend fun fromEntropy(size: EntropySize, entropy: ByteArray): CharArray {
        val wordlist = repo.getWords()
        val hash = Cryptography.Hash.sha256(entropy)

        val totalBits = size.bytes * 8 + size.checksumLengthBits
        val wordCount = totalBits / 11

        val indices = IntArray(wordCount)
        for (i in 0 until wordCount) {
            var index = 0
            for (j in 0 until 11) {
                val bitPos = i * 11 + j
                val bit = if (bitPos < size.bytes * 8) {
                    getBit(entropy, bitPos)
                } else {
                    getBit(hash, bitPos - size.bytes * 8)
                }
                index = (index shl 1) or bit
            }
            indices[i] = index
        }

        var totalSize = wordCount - 1
        for (i in 0 until wordCount) {
            totalSize += wordlist[indices[i]].length
        }

        val result = CharArray(totalSize)
        var pos = 0
        for (i in 0 until wordCount) {
            if (i > 0) {
                result[pos++] = ' '
            }
            val word = wordlist[indices[i]]
            for (c in word) {
                result[pos++] = c
            }
        }

        hash.fill(0)
        indices.fill(0)

        return result
    }

    private fun getBit(bytes: ByteArray, bitIndex: Int): Int {
        val byteIndex = bitIndex / 8
        val bitOffset = 7 - (bitIndex % 8)
        return (bytes[byteIndex].toInt() shr bitOffset) and 1
    }
}

