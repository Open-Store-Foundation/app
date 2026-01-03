package foundation.openstore.gcip.sdk

import foundation.openstore.gcip.core.ClientRequest
import foundation.openstore.gcip.core.ClientRequestData
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.ExchangeKey
import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.data.GcipNonceProvider
import foundation.openstore.gcip.core.data.GcipNonceProviderAtomic
import foundation.openstore.gcip.core.encryption.EcdhEncryptionProvider
import foundation.openstore.gcip.core.encryption.KeyPair
import foundation.openstore.gcip.core.handler.GcipHandlerFactory
import foundation.openstore.gcip.core.util.GcipErrorContext
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrCtx
import foundation.openstore.gcip.encryption.EcdhProviderDefault
import foundation.openstore.gcip.encryption.GcipEncryptionFactory

class GcipWallet(
    private val delegate: Delegate,
) {
    
    interface Delegate {
        suspend fun getSessionKey(eid: GcipId): ByteArray?
        fun handleError(error: GcipErrorContext)
    }
    
    class GcipEncryptionStorage(
        private val encryption: EcdhEncryptionProvider,
        private val nonceProvider: GcipNonceProvider,
    ) {
        private val pairs = mutableMapOf<UShort, KeyPair>()
        var latestKeyPair: KeyPair? = null

        fun nonce(): UShort {
            return nonceProvider.generate()
        }

        suspend fun generateExchangeKey(nonce: UShort): ExchangeKey {
            val pair = encryption.generatePair()
            pairs[nonce] = pair
            latestKeyPair = pair
            return pair.getExchangeKey()
        }

        fun handshakePrivateKey(nonce: UShort): KeyPair? {
            return pairs.remove(nonce)
        }
    }

    private val storage = GcipEncryptionStorage(
        encryption = EcdhProviderDefault(),
        nonceProvider = GcipNonceProviderAtomic()
    )

    private val encryption = GcipEncryptionFactory.create(
        object : GcipEncryptionCoder.Delegate {
            override suspend fun popHandshakePrivateKey(nonce: UShort): KeyPair? {
                return storage.handshakePrivateKey(nonce)
            }

            override suspend fun getSessionKey(eid: GcipId): ByteArray? {
                return delegate.getSessionKey(eid)
            }
        }
    )

    suspend fun deriveSessionKey(response: CommonResponse.Exchange): ByteArray? {
        val key = storage.latestKeyPair ?: return null
        return encryption.generateSessionKey(
            eid = response.encryption.eid,
            keyPair = KeyPair(
                pk = key.pk,
                pub = response.encryption.key.payload,
            ),
        )
    }

    suspend fun deriveSessionKey(response: CommonResponse.Connect.Handshake): ByteArray? {
        val key = storage.latestKeyPair ?: return null
        return encryption.generateSessionKey(
            eid = response.encryption.eid,
            keyPair = KeyPair(
                pk = key.pk,
                pub = response.encryption.key.payload,
            ),
        )
    }



    private val walletHandler = GcipHandlerFactory.defaultClientHandler(
        encryption = encryption,
    )
    
    suspend fun createRequestData(data: ClientRequestData): GcipResult<ByteArray> {
        val request = createRequest(data)
        val data = walletHandler.createRequest(request)
        return data
    }

    suspend fun getResponse(data: ByteArray): CommonResponse? {
        val response = walletHandler.retrieveResponse(data)
            .getOrCtx { ctx ->
                delegate.handleError( ctx)
                return null
            }

        return response
    }
    
    private suspend fun createRequest(data: ClientRequestData): ClientRequest {
        val nonce = storage.nonce()

        val request = when (data) {
            is ClientRequestData.Exchange -> ClientRequest.Exchange(
                data = data,
                encryption = Encryption.Handshake.Request(storage.generateExchangeKey(nonce)),
                nonce = nonce
            )
            is ClientRequestData.Connect -> ClientRequest.Connect(
                data = data,
                encryption = when (val eid = data.eid) {
                    null -> Encryption.Handshake.Request(storage.generateExchangeKey(nonce))
                    else -> Encryption.Session(eid)
                },
                nonce = nonce,
            )
            is ClientRequestData.Disconnect -> ClientRequest.Disconnect(
                data = data,
                encryption = Encryption.Session(data.eid),
                nonce = nonce,
            )
            is ClientRequestData.Extend -> ClientRequest.Extend(
                data = data,
                encryption = Encryption.Session(data.eid),
                nonce = nonce,
            )
            is ClientRequestData.Sign -> ClientRequest.Sign(
                data = data,
                encryption = Encryption.Session(data.eid),
                nonce = nonce,
            )
        }

        return request
    }
}
