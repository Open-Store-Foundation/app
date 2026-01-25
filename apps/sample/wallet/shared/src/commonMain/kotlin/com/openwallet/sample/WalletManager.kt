package com.openwallet.sample

import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.transport.GcipId

class WalletManager(private val repository: WalletRepository) {

    /**
     * Handles the GCIP response and updates the wallet repository accordingly.
     * Returns the connection ID that was pending if the operation requires cleanup, or null.
     */
    suspend fun handleResponse(response: CommonResponse?, connectionId: GcipId?): GcipId? {
        if (response == null) return null

        when (response) {
            is CommonResponse.Exchange -> {}
            is CommonResponse.Connect -> {
                val connectionId = response.data.connectionId
                response.data.wallets.forEach { gcipWallet ->
                    val creds = gcipWallet.credentials.map {
                        CredentialInfo(it.id, it.derivation.derivation)
                    }

                    if (creds.isNotEmpty()) {
                        val wallet = Wallet(
                            id = GcipId.generate(),
                            name = "Wallet ${gcipWallet.namespace ?: ""}",
                            connectionId = connectionId, // Store the connectionId for future requests
                            credentials = creds,
                            isVerified = true
                        )
                        repository.addWallet(wallet)
                    }
                }
                return null
            }
            is CommonResponse.Sign -> {
                // Success, nothing to update in repo
                return null
            }
            is CommonResponse.Extend -> {
                val connectionId = connectionId
                if (connectionId != null) {
                    val currentWallets = repository.getWallets()
                    val existingWallet = currentWallets.find { it.connectionId == connectionId }

                    if (existingWallet != null) {
                        val newCredentials = response.wallets.flatMap { w ->
                            w.credentials.map { CredentialInfo(it.id, it.derivation.derivation) }
                        }

                        val mergedCredentials = (existingWallet.credentials + newCredentials).distinctBy { it.id }

                        val updatedWallet = existingWallet.copy(
                            credentials = mergedCredentials
                        )

                        repository.updateWallet(updatedWallet)
                    }
                }
                return connectionId // Return ID to indicate it was processed/cleared
            }
            is CommonResponse.Disconnect -> {
                val connectionId = connectionId
                if (connectionId != null) {
                    repository.removeWallet(connectionId)
                }
                return connectionId
            }

        }
        return null
    }
}
