package foundation.openstore.signer.app.data.passcode

import com.openstore.app.core.common.fillZeros
import com.openstore.app.core.common.use
import com.openstore.core.signing.Cryptography
import foundation.openstore.signer.app.data.mnemonic.Mnemonic
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

data class PinCredential(
    val salt: ByteArray,
    val iv: ByteArray,
    val encryptedData: ByteArray
) : AutoCloseable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PinCredential
        if (!salt.contentEquals(other.salt)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (!encryptedData.contentEquals(other.encryptedData)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = salt.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + encryptedData.contentHashCode()
        return result
    }

    override fun close() {
        salt.fillZeros()
        iv.fillZeros()
        encryptedData.fillZeros()
    }
}

@OptIn(ExperimentalAtomicApi::class)
class SecureStore internal constructor(
    private val source: ByteArray,
) {

    private val isDisposed = AtomicBoolean(false)

    suspend fun encryptMnemonic(mnemonic: Mnemonic): ByteArray {
        val result = source.use { source ->
            mnemonic.bytes().use { bytes ->
                encrypt(bytes, source)
            }
        }

        isDisposed.compareAndSet(false,  true)

        return result
    }

    suspend fun decryptMnemonic(encryptedMnemonic: ByteArray): Mnemonic {
        val result = source.use { source ->
            decrypt(encryptedMnemonic, source).use { bytes ->
                Mnemonic.from(bytes)
            }
        }

        isDisposed.compareAndSet(false, true)

        return result
    }

    fun isDisposed(): Boolean = isDisposed.load()

    private suspend fun encrypt(data: ByteArray, source: ByteArray): ByteArray {
        val result = Cryptography.Aes.gcmEncrypt(source, data)
        return result.iv + result.encryptedData
    }

    private suspend fun decrypt(encryptedData: ByteArray, source: ByteArray): ByteArray {
        val iv = encryptedData.copyOfRange(0, IV_LENGTH_BYTES)
        val cipherText = encryptedData.copyOfRange(IV_LENGTH_BYTES, encryptedData.size)
        return Cryptography.Aes.gcmDecrypt(source, iv, cipherText)
    }

    companion object {
        const val IV_LENGTH_BYTES = 12
    }
}

object PinCrypto {

    const val ITERATIONS: Int = 50_000
    const val KEY_LENGTH_BITS: Int = 256
    const val SALT_LENGTH_BYTES: Int = 32

    suspend fun seal(pin: CharArray, dataToProtect: ByteArray): PinCredential {
        val salt = Cryptography.Random.secureRandomBytes(SALT_LENGTH_BYTES)

        return deriveKey(pin, salt).use { key ->
            val result = Cryptography.Aes.gcmEncrypt(key, dataToProtect)
            PinCredential(salt, result.iv, result.encryptedData)
        }
    }

    suspend fun open(pin: CharArray, box: PinCredential): ByteArray {
        return deriveKey(pin, box.salt).use {
            Cryptography.Aes.gcmDecrypt(it, box.iv, box.encryptedData)
        }
    }

    suspend fun createStore(pin: CharArray, salt: ByteArray): SecureStore {
        return SecureStore(deriveKey(pin, salt))
    }

    private suspend fun deriveKey(pin: CharArray, salt: ByteArray): ByteArray {
        return Cryptography.Hash.pbkdf2WithHmacSHA512(pin, salt, ITERATIONS, KEY_LENGTH_BITS)
    }
}
