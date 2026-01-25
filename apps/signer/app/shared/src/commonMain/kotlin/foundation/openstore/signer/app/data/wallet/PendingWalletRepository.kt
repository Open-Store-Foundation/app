@file:Suppress("ArrayInDataClass")

package foundation.openstore.signer.app.data.wallet

import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.transport.Identifier
import foundation.openstore.signer.app.data.mnemonic.Mnemonic

sealed interface PendingAction {
    val mnemonic: Mnemonic

    data class Create(
        val name: String,
        override val mnemonic: Mnemonic,
    ) : PendingAction

    data class Verify(
        val walletId: GcipId,
        override val mnemonic: Mnemonic
    ) : PendingAction
}

class PendingWalletRepository {
    private val requests = mutableMapOf<String, PendingAction>()

    fun putPendingCreation(name: String, mnemonic: Mnemonic): String {
        val id = Identifier.generateId()
        requests[id] = PendingAction.Create(name, mnemonic)
        return id
    }

    fun putPendingVerification(walletId: GcipId, mnemonic: Mnemonic): String {
        val id = Identifier.generateId()
        requests[id] = PendingAction.Verify(walletId, mnemonic)
        return id
    }

    fun getPending(id: String): PendingAction? {
        return requests[id]
    }

    fun clear(id: String) {
       requests.remove(id)
           ?.mnemonic
           ?.close()
    }

    fun clearAll() {
        requests.forEach { it.value.mnemonic.close() }
        requests.clear()
    }
}
