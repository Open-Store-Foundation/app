package foundation.openstore.gcip.core.handler

import foundation.openstore.gcip.core.ClientRequest
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.core.coder.GcipBlock
import foundation.openstore.gcip.core.coder.GcipCborCoder
import foundation.openstore.gcip.core.coder.GcipCoder
import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.handler.internal.EncryptionMessageParams
import foundation.openstore.gcip.core.handler.internal.getEncryptionMessageType
import foundation.openstore.gcip.core.handler.internal.toSignerBlock
import foundation.openstore.gcip.core.transport.GcipConnectResponse
import foundation.openstore.gcip.core.transport.GcipDisconnectResponse
import foundation.openstore.gcip.core.transport.GcipExchangeResponse
import foundation.openstore.gcip.core.transport.GcipExtendResponse
import foundation.openstore.gcip.core.transport.GcipSignResponse
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrCtx
import foundation.openstore.gcip.core.util.getOrError
import foundation.openstore.gcip.core.validator.GcipCommonValidator
import foundation.openstore.gcip.core.validator.GcipWalletValidatorDefault

// TODO check all keys length
@Suppress("UNCHECKED_CAST")
class GcipWalletHandler(
    private val cbor: GcipCborCoder,
    private val encryptor: GcipEncryptionCoder,
    private val version: UByte = GcipConfig.ActualVersion,
) {

    private val commonValidator = GcipCommonValidator(cbor)
    private val validator = GcipWalletValidatorDefault()

    suspend fun createRequest(request: ClientRequest): GcipResult<ByteArray> {
        val method = commonValidator.requestToMethod(request)

        val header = GcipBlock.Header(
            version = version,
            status = GcipStatus.Success,
            nonce = request.nonce,
            method = method,
            dataLength = 0,
        )

        val signature = GcipCoder.headerSignature(header)
        val requestData = cbor.encodeClientRequest(request)

        val initialEncryption = when (request) {
            is ClientRequest.Exchange -> request.encryption
            is ClientRequest.Connect -> when (val exchange = request.encryption) {
                is Encryption.Handshake.Request -> exchange
                is Encryption.Session -> exchange
            }
            is ClientRequest.Extend,
            is ClientRequest.Sign,
            is ClientRequest.Disconnect, -> request.encryption
        }

        val iv = encryptor.generateIv()
        val result = when (initialEncryption) {
            is Encryption.Handshake.Request -> cbor.encodeEncryptedMessage(
                encryption = initialEncryption,
                iv = iv,
                rawData = requestData,
            )
            is Encryption.Session -> cbor.encodeEncryptedMessage(
                encryption = initialEncryption,
                iv = iv,
                rawData = encryptor.encrypt(
                    eid = initialEncryption.eid,
                    data = requestData,
                    iv = iv,
                    aad = encryptor.generateAad(
                        signature = signature,
                        eid = initialEncryption.eid,
                    )
                ).getOrError { return it }
            )
        }

        val data = result.getOrError { return it }

        return GcipResult.ok(
            GcipCoder.encodeBlock(header.copy(dataLength = data.size), data)
        )
    }

    suspend fun retrieveResponse(data: ByteArray): GcipResult<CommonResponse> {
        val block = GcipCoder.decodeBlock(data)
            .getOrError { return it }

        val error = GcipResult.builder<CommonResponse>(block = block)

        val method = block.method
        val signature = GcipCoder.headerSignature(block.header)

        val signerBlock = block.toSignerBlock()
            .getOrCtx { return error.with(it) }

        val message = commonValidator.validateResponseMethod(block.method, block.data)
            .getOrCtx { return error.with(it) }

        val encryptionParams = message.getEncryptionMessageType()
            .getOrCtx { return error.with(it) }

        val encryption = when (encryptionParams) {
            is EncryptionMessageParams.Plain -> return error.with(GcipStatus.InvalidFormat)
            is EncryptionMessageParams.Encrypted -> encryptionParams.encryption
        }

        val data = when (encryption) {
            is Encryption.Handshake.Response,
            is Encryption.Session -> encryptor.decrypt(
                nonce = block.nonce,
                eid = encryption.eid,
                data = message.data,
                iv = encryptionParams.iv,
                ekey = encryption.key?.payload,
                aad = encryptor.generateAad(signature = signature, eid = encryption.eid),
            ).getOrCtx {
                return error.with(it)
            }
        }

        val response = cbor.decodeResponse(method, data)
            .getOrCtx { return error.with(it) }

        val result =  when (encryption) {
            is Encryption.Handshake.Response -> {
                when (response) {
                    is GcipExchangeResponse -> validator.validate(signerBlock, response, encryption)
                    is GcipConnectResponse -> validator.validate(signerBlock, response, encryption)
                    else -> return error.with(GcipStatus.InvalidFormat)
                }
            }
            is Encryption.Session -> {
                when (response) {
                    is GcipExtendResponse -> validator.validate(signerBlock, response, encryption)
                    is GcipSignResponse -> validator.validate(signerBlock, response, encryption)
                    is GcipDisconnectResponse -> validator.validate(signerBlock, response, encryption)
                    else -> return error.with(GcipStatus.InvalidFormat)
                }
            }
        }

        return GcipResult.ok(
            result.getOrCtx { return error.with(it) }
        )
    }
}
