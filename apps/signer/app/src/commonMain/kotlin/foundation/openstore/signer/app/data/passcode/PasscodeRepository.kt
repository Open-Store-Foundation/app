package foundation.openstore.signer.app.data.passcode

import com.openstore.app.core.common.use
import com.openstore.app.core.root.DeviceRootProvider
import foundation.openstore.signer.app.utils.TimeGenerator
import kotlin.math.max

sealed class PasscodeResult {
    data class Success(val store: SecureStore) : PasscodeResult()
    data object WrongPin : PasscodeResult()
    data object Locked : PasscodeResult()
    data object Wipe : PasscodeResult()
}

class PasscodeRepository(
    val passwordStore: PasswordRepository,
    val timer: TimeGenerator,
    val rootProvider: DeviceRootProvider?,
) {

    val isSecure: Boolean get() {
        return rootProvider
            ?.isRooted()
            ?.not()
            ?: true
    }

    suspend fun verify(pinChars: CharArray): PasscodeResult {
        var failCounter = passwordStore.config(PasswordRepository.FAIL_COUNTER_KEY)

        val diff: Long = unlockTime() - timer.getZonelessTime()
        if (failCounter >= LOCK_TRIES || diff > 0) {
            if (diff > MAX_DIFF) {
                passwordStore.setConfig(PasswordRepository.LOCK_KEY, timer.getZonelessTime())
                resetFailCounter()
            }

            return PasscodeResult.Locked
        }

        val store = verifyAndGetStore(pinChars)
        if (store != null) {
            resetFailCounter()
            return PasscodeResult.Success(store)
        } else {
            failCounter++
            passwordStore.setConfig(PasswordRepository.FAIL_COUNTER_KEY, failCounter)

//                if (failCounter >= WIPE_TRIES) { // TODO
//                    return PasscodeResult.Wipe
//                }

            if (failCounter >= LOCK_TRIES) {
                passwordStore.setConfig(PasswordRepository.LOCK_KEY, timer.getZonelessTime() + LOCK_TIME)
                return PasscodeResult.Locked
            }
            return PasscodeResult.WrongPin
        }
    }

    suspend fun set(pinChars: CharArray): SecureStore {
        val store = pinChars.use { pin ->
            PinCrypto.seal(pin, VERIFICATION_DATA).use { box ->
                passwordStore.setBlobPassword(PasswordRepository.SALT_KEY, box.salt)
                passwordStore.setBlobPassword(PasswordRepository.IV_KEY, box.iv)
                passwordStore.setBlobPassword(PasswordRepository.DATA_KEY, box.encryptedData)

                PinCrypto.createStore(pin, box.salt)
            }
        }

        resetFailCounter()

        return store
    }

    private suspend fun verifyAndGetStore(pinChars: CharArray): SecureStore? {
        val salt = passwordStore.blobPassword(PasswordRepository.SALT_KEY) ?: return null
        val iv = passwordStore.blobPassword(PasswordRepository.IV_KEY) ?: return null
        val encryptedData = passwordStore.blobPassword(PasswordRepository.DATA_KEY) ?: return null

        return pinChars.use { pin ->
            PinCredential(salt, iv, encryptedData).use { box ->
                runCatching {
                    PinCrypto.open(pin, box).use { decrypted ->
                        if (decrypted.contentEquals(VERIFICATION_DATA)) {
                            PinCrypto.createStore(pin, salt)
                        } else {
                            null
                        }
                    }
                }.getOrNull()
            }
        }
    }

    suspend fun timeToUnlock(): Long {
        val diff: Long = unlockTime() - timer.getZonelessTime()
        if (diff > MAX_DIFF) {
            passwordStore.setConfig(PasswordRepository.LOCK_KEY, timer.getZonelessTime())
            return 0
        }
        return max(0, diff / 1000L)
    }

    suspend fun unlockTime(): Long {
        return runCatching { passwordStore.config(PasswordRepository.LOCK_KEY) }
            .getOrDefault(0L)
    }

    suspend fun has(): Boolean {
        return runCatching { passwordStore.has(PasswordRepository.SALT_KEY) }
            .getOrDefault(false)
    }

    @Throws(Exception::class)
    suspend fun delete() {
        passwordStore.remove(PasswordRepository.SALT_KEY)
        passwordStore.remove(PasswordRepository.IV_KEY)
        passwordStore.remove(PasswordRepository.DATA_KEY)

        resetFailCounter()
    }

    private suspend fun resetFailCounter() {
        passwordStore.setConfig(PasswordRepository.FAIL_COUNTER_KEY, 0)
    }

    companion object {
        const val LOCK_TIME: Long = 1000 * 60 * 1
        const val MAX_DIFF: Long = LOCK_TIME * 5
        const val LOCK_TRIES: Int = 5
        private val VERIFICATION_DATA = "OPENSTORE_PIN_VERIFICATION".encodeToByteArray()
    }
}
