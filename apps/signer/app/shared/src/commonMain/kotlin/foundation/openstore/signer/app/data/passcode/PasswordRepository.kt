package foundation.openstore.signer.app.data.passcode

import com.openstore.app.store.common.store.KeyValueStorage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface PasswordRepository {
    companion object {
        const val SALT_KEY: String = "pin_salt"
        const val IV_KEY: String = "pin_iv"
        const val DATA_KEY: String = "pin_data"
        const val LOCK_KEY: String = "lock"
        const val FAIL_COUNTER_KEY: String = "fail"
    }

    suspend fun setBlobPassword(alias: String, data: ByteArray)
    suspend fun blobPassword(alias: String): ByteArray?

    suspend fun config(alias: String): Long
    suspend fun setConfig(alias: String, value: Long)

    suspend fun has(alias: String): Boolean
    suspend fun remove(alias: String)
}

@OptIn(ExperimentalEncodingApi::class)
class PasswordStoreImpl(
    private val storage: KeyValueStorage
) : PasswordRepository {

    override suspend fun setBlobPassword(alias: String, data: ByteArray) {
        storage.putString(alias, Base64.encode(data))
    }

    override suspend fun blobPassword(alias: String): ByteArray? {
        return storage.getStringOrNull(alias)?.let { Base64.decode(it) }
    }

    override suspend fun config(alias: String): Long {
        return storage.getLong(alias, 0L)
    }

    override suspend fun setConfig(alias: String, value: Long) {
        storage.putLong(alias, value)
    }

    override suspend fun has(alias: String): Boolean {
        return storage.hasKey(alias)
    }

    override suspend fun remove(alias: String) {
        storage.remove(alias)
    }
}
