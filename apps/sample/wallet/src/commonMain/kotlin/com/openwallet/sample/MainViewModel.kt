package com.openwallet.sample

import com.openstore.app.mvi.AsyncViewModel
import foundation.openstore.gcip.core.Challenge
import foundation.openstore.gcip.core.ClientData
import foundation.openstore.gcip.core.ClientRequestData
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.Transport
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.util.GcipErrorContext
import foundation.openstore.gcip.core.util.getOrNull
import foundation.openstore.gcip.sdk.GcipWallet
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel(
    private val repository: WalletRepository
) : AsyncViewModel() {

    private val walletManager = WalletManager(repository)

    private val sdk = GcipWallet(
        object : GcipWallet.Delegate {
            override suspend fun getSessionKey(eid: GcipId): ByteArray? {
                val session = repository.getSessionByEid(eid)
                return session?.sessionKey
            }

            override fun handleError(error: GcipErrorContext) {
                println("Error: $error")
                stateScope.launch {
                    _messages.send("Error: $error")
                }
            }
        }
    )

    private val _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets = _wallets.asStateFlow()

    private val _exchangeSessions = MutableStateFlow<List<WalletSession>>(emptyList())
    val exchangeSessions = _exchangeSessions.asStateFlow()

    private val _requests = Channel<ByteArray>(Channel.BUFFERED)
    val requests = _requests.receiveAsFlow()

    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()


    init {
        refreshWallets()
        refreshExchangeSessions()
    }

    private fun refreshWallets() {
        stateScope.launch {
            _wallets.value = repository.getWallets()
        }
    }

    private fun refreshExchangeSessions() {
        stateScope.launch {
            _exchangeSessions.value = repository.getSessions().filter { it.connectionId == null }
        }
    }

    fun handleResponseData(data: ByteArray?) {
        if (data == null) {
            _messages.trySend("Invalid response data")
            return
        }

        stateScope.launch {
            val response = sdk.getResponse(data) ?: return@launch
            handleResponse(response)
        }
    }

    private suspend fun handleResponse(response: CommonResponse) {
        when (response) {
            is CommonResponse.Exchange -> {
                val sessionKey = sdk.deriveSessionKey(response)
                if (sessionKey == null) {
                    _messages.trySend("Missing exchange key")
                    return
                }

                repository.addSession(
                    WalletSession(
                        eid = response.encryption.eid,
                        sessionKey = sessionKey,
                        connectionId = null,
                    )
                )
                refreshExchangeSessions()
                _messages.trySend("Exchange session created")
            }
            is CommonResponse.Connect -> {
                walletManager.handleResponse(response, null)
                when (response) {
                    is CommonResponse.Connect.Handshake -> {
                        val sessionKey = sdk.deriveSessionKey(response)
                        if (sessionKey != null) {
                            repository.addSession(
                                WalletSession(
                                    eid = response.encryption.eid,
                                    sessionKey = sessionKey,
                                    connectionId = response.data.connectionId,
                                )
                            )
                        }
                    }
                    is CommonResponse.Connect.Session -> {
                        val existing = repository.getSessionByEid(response.encryption.eid)
                        if (existing != null) {
                            repository.addSession(existing.copy(connectionId = response.data.connectionId))
                        }
                    }
                }
                refreshWallets()
                refreshExchangeSessions()
                _messages.trySend("Wallet imported!")
            }
            is CommonResponse.Sign -> {
                _messages.trySend("Data signed successfully!")
            }
            is CommonResponse.Extend -> {
                walletManager.handleResponse(response, response.connectionId)
                refreshWallets()
                _messages.trySend("Extended successfully!")
            }
            is CommonResponse.Disconnect -> {
                walletManager.handleResponse(response, response.connectionId)
                refreshWallets()
                _messages.trySend("Disconnected successfully!")
            }
        }
    }

    fun importWallet(selectedDerivations: List<Derivation>) {
        stateScope.launch {
            try {


                val request = sdk.createRequestData(
                    ClientRequestData.Connect(
                        eid = null,
                        credentials = selectedDerivations,
                        transport = Transport.Internal,
                        clientData = ClientData(
                            name = "Simple Wallet",
                            origin = "https://example.com"
                        ),
                    )
                ).getOrNull()

                if (request != null) {
                    _requests.send(request)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messages.send("Error starting import: ${e.message}")
            }
        }
    }

    fun connectExchangeSession(eid: GcipId, selectedDerivations: List<Derivation>) {
        stateScope.launch {
            try {
                val request = sdk.createRequestData(
                    ClientRequestData.Connect(
                        clientData = ClientData(
                            name = "Simple Wallet",
                            origin = "https://example.com"
                        ),
                        credentials = selectedDerivations,
                        eid = eid,
                        transport = Transport.Internal,
                    )
                ).getOrNull()
                
                if (request != null) {
                    _requests.send(request)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messages.send("Error starting connect: ${e.message}")
            }
        }
    }

    fun requestExchange(transport: Transport) {
        stateScope.launch {
            try {
                val request = sdk.createRequestData(
                    ClientRequestData.Exchange(
                        transport = transport
                    )
                ).getOrNull()

                if (request != null) {
                    _requests.send(request)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messages.send("Error starting exchange: ${e.message}")
            }
        }
    }

    fun sign(wallet: Wallet, cred: CredentialInfo) {
        stateScope.launch {
            try {
                val session = repository.getSessions().find { it.connectionId == wallet.connectionId }
                if (session == null) {
                    _messages.send("No session found for wallet")
                    return@launch
                }

                val request = sdk.createRequestData(
                    ClientRequestData.Sign(
                        eid = session.eid,
                        credentialId = cred.id,
                        challenge = Challenge(
                            rawData = ByteArray(32).apply { Random.nextBytes(this) }
                        ),
                        connectionId = wallet.connectionId
                    )
                ).getOrNull()

                if (request != null) {
                    _requests.send(request)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messages.send("Error starting sign: ${e.message}")
            }
        }
    }

    fun disconnect(wallet: Wallet) {
        stateScope.launch {
            try {
                val session = repository.getSessions().find { it.connectionId == wallet.connectionId }
                if (session == null) {
                    _messages.send("No session found for wallet")
                    return@launch
                }

                val request = sdk.createRequestData(
                    ClientRequestData.Disconnect(
                        eid = session.eid,
                        connectionId = wallet.connectionId,
                    )
                ).getOrNull()

                if (request != null) {
                    _requests.send(request)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messages.send("Error starting disconnect: ${e.message}")
            }
        }
    }

    fun extend(wallet: Wallet, selectedDerivations: List<Derivation>) {
        stateScope.launch {
            try {
                val session = repository.getSessions().find { it.connectionId == wallet.connectionId }
                if (session == null) {
                     _messages.send("No session found for wallet")
                    return@launch
                }

                val request = sdk.createRequestData(
                    ClientRequestData.Extend(
                        eid = session.eid,
                        credentials = selectedDerivations,
                        connectionId = wallet.connectionId,
                    )
                ).getOrNull()

                if (request != null) {
                    _requests.send(request)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messages.send("Error starting extend: ${e.message}")
            }
        }
    }
}