package com.openwallet.sample

import com.openstore.app.store.common.store.KeyValueStorageWrapper
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.russhwolf.settings.Settings
import foundation.openstore.gcip.core.transport.GcipId

@Serializable
data class CredentialInfo(
    val id: GcipId,
    val derivation: String?
)

@Serializable
data class Wallet(
    val id: GcipId,
    val name: String,
    val credentials: List<CredentialInfo>,
    val connectionId: GcipId,
    val isVerified: Boolean = false
)

class WalletRepository(private val settings: Settings) {
    private val storage = KeyValueStorageWrapper(settings)
    private val json = Json { ignoreUnknownKeys = true }
    
    private val KEY_WALLETS = "wallets_list"

    suspend fun getWallets(): List<Wallet> {
        val jsonString = storage.getStringOrNull(KEY_WALLETS) ?: return emptyList()
        return try {
            json.decodeFromString<List<Wallet>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addWallet(wallet: Wallet) {
        val current = getWallets()
            .toMutableList()
        current.add(wallet)
        storage.putString(KEY_WALLETS, json.encodeToString(current))
    }

    suspend fun updateWallet(wallet: Wallet) {
        val current = getWallets().toMutableList()
        val index = current.indexOfFirst { it.connectionId == wallet.connectionId }
        if (index != -1) {
            current[index] = wallet
            storage.putString(KEY_WALLETS, json.encodeToString(current))
        }
    }

    suspend fun removeWallet(connectionId: GcipId) {
        val current = getWallets().toMutableList()
        current.removeAll { it.connectionId == connectionId }
        storage.putString(KEY_WALLETS, json.encodeToString(current))
    }

    /* Session Management */
    private val KEY_SESSIONS = "sessions_list"

    suspend fun getSessions(): List<WalletSession> {
        val jsonString = storage.getStringOrNull(KEY_SESSIONS) ?: return emptyList()
        return try {
            json.decodeFromString<List<WalletSession>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addSession(session: WalletSession) {
        val current = getSessions().toMutableList()
        current.removeAll { it.eid == session.eid }
        current.add(session)
        storage.putString(KEY_SESSIONS, json.encodeToString(current))
    }

    suspend fun getSessionByEid(eid: GcipId): WalletSession? {
        return getSessions().find { it.eid == eid }
    }

    suspend fun removeSession(eid: GcipId) {
        val current = getSessions().toMutableList()
        current.removeAll { it.eid == eid }
        storage.putString(KEY_SESSIONS, json.encodeToString(current))
    }
}

@Suppress("ArrayInDataClass")
@Serializable
data class WalletSession(
    val eid: GcipId,
    val sessionKey: ByteArray?,
    val connectionId: GcipId? = null
)
