package foundation.openstore.signer.app.data.passcode

import com.openstore.app.store.common.store.KeyValueStorage

interface SecurityRepository {
    suspend fun isPasscodeEnabled(): Boolean
    suspend fun setPasscodeEnabled(enabled: Boolean)

    suspend fun setLockMethod(method: LockMethod)
    suspend fun getLockMethod(): LockMethod
}

class SecurityRepositoryImpl(
    private val keyStore: KeyValueStorage
) : SecurityRepository {

    override suspend fun isPasscodeEnabled(): Boolean {
        return keyStore.getBoolean("passcode_enabled", false)
    }

    override suspend fun setPasscodeEnabled(enabled: Boolean) {
        return keyStore.putBoolean("passcode_enabled", enabled)
    }

    override suspend fun setLockMethod(method: LockMethod) {
        return keyStore.putString("lock_method", method.name)
    }

    override suspend fun getLockMethod(): LockMethod {
        return LockMethod.valueOf(keyStore.getString("lock_method", LockMethod.PIN.name))
    }
}