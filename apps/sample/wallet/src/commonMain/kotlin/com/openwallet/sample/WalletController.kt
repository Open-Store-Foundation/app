package com.openwallet.sample

import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.transport.GcipStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletController(
    private val repository: WalletRepository,
    private val delegate: WalletPlatformDelegate,
    private val scope: CoroutineScope
) {
    private val _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets = _wallets.asStateFlow()

    private val walletManager = WalletManager(repository)

    // Used for extend/disconnect flow where we need context of which wallet was acted upon
    private var pendingConnectionId: GcipId? = null

    init {
        loadWallets()
    }

    private fun loadWallets() {
        scope.launch {
            _wallets.value = repository.getWallets()
        }
    }

    fun onImportWallet(derivations: List<Derivation>) {
        delegate.importWallet(derivations) { code, response ->
            scope.launch {
                handleResponse(code, response)
            }
        }
    }

    fun onSign(wallet: Wallet, cred: CredentialInfo) {
        delegate.sign(wallet.connectionId.fmt, cred.id.fmt) { code, response ->
            scope.launch {
                handleResponse(code, response)
            }
        }
    }

    fun onDisconnect(wallet: Wallet) {
        pendingConnectionId = wallet.connectionId
        delegate.disconnect(wallet.connectionId.fmt) { code, response ->
            scope.launch {
                handleResponse(code, response)
            }
        }
    }

    fun onExtend(wallet: Wallet, derivations: List<Derivation>) {
        pendingConnectionId = wallet.connectionId
        delegate.extend(wallet.connectionId.fmt, derivations) { code, response ->
            scope.launch {
                handleResponse(code, response)
            }
        }
    }

    private suspend fun handleResponse(code: GcipStatus, response: CommonResponse?) {
        println("Code: $code, Response: $response")

        if (response == null) return

        when (response) {
            is CommonResponse.Exchange -> {}
            is CommonResponse.Connect -> {
                walletManager.handleResponse(response, null)
                loadWallets()
            }
            is CommonResponse.Sign -> {
                // Success
            }
            is CommonResponse.Extend -> {
                val result = walletManager.handleResponse(response, pendingConnectionId)
                loadWallets()
                pendingConnectionId = result // Should be null if successful, or we just clear it
                pendingConnectionId = null
            }
            is CommonResponse.Disconnect -> {
                val result = walletManager.handleResponse(response, pendingConnectionId)
                loadWallets()
                pendingConnectionId = result
                pendingConnectionId = null
            }
        }
    }
}


