package foundation.openstore.gcip.core.handler

import foundation.openstore.gcip.core.GcipConfig
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.gcip.core.coder.GcipBlock
import foundation.openstore.gcip.core.coder.GcipCoder
import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.data.GcipDeviceProvider
import foundation.openstore.gcip.core.encryption.HashingProvider
import foundation.openstore.gcip.core.Encryption
import foundation.openstore.gcip.core.handler.internal.toSignerBlock
import foundation.openstore.gcip.core.coder.GcipCborCoder
import foundation.openstore.gcip.core.handler.internal.EncryptionMessageParams
import foundation.openstore.gcip.core.handler.internal.getEncryptionMessageType
import foundation.openstore.gcip.core.transport.GcipConnectRequest
import foundation.openstore.gcip.core.transport.GcipDisconnectRequest
import foundation.openstore.gcip.core.transport.GcipExchangeRequest
import foundation.openstore.gcip.core.transport.GcipExtendRequest
import foundation.openstore.gcip.core.transport.GcipMethod
import foundation.openstore.gcip.core.transport.GcipSignRequest
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipErrorContext
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrCtx
import foundation.openstore.gcip.core.util.getOrError
import foundation.openstore.gcip.core.util.toUrlBase64Fmt
import foundation.openstore.gcip.core.validator.GcipCommonValidator
import foundation.openstore.gcip.core.validator.GcipSignerValidator

class GcipSignerHandler(
    private val cbor: GcipCborCoder,
    private val encryptor: GcipEncryptionCoder,
    private val validator: GcipSignerValidator,
    private val device: GcipDeviceProvider,
    private val hasher: HashingProvider,
    private val version: UByte = GcipConfig.ActualVersion,
) {

    private val messageCoder = GcipCommonValidator(cbor)

    fun <T> createError(ctx: GcipResult.Error<T>): ByteArray {
        return createError(ctx.ctx)
    }

    fun createError(ctx: GcipErrorContext): ByteArray {
        return createError(ctx.error, ctx.block)
    }

    private fun createError(error: GcipStatus, block: GcipBlock?): ByteArray {
        return createError(
            statusCode = error,
            nonce = block?.nonce ?: 0u,
            method = block?.method?.let(messageCoder::requestToResponse),
        )
    }

    private fun createError(statusCode: GcipStatus, nonce: UShort, method: GcipMethod?): ByteArray {
        return GcipCoder.encodeBlock(
            header = GcipBlock.Header(
                version = version,
                status = statusCode,
                nonce = nonce,
                method = method,
                dataLength = 0,
            ),
            data = byteArrayOf()
        )
    }

    suspend fun createResponse(response: CommonResponse): GcipResult<ByteArray> {
        val header = GcipBlock.Header(
            version = version,
            status = GcipStatus.Success,
            nonce = response.block.nonce,
            method = messageCoder.requestToResponse(response.block.method),
            dataLength = 0,
        )

        val signature = GcipCoder.headerSignature(header)
        val binaryResponse = cbor.encodeResponse(response)
            .getOrError { return it }

        val iv = encryptor.generateIv()
        val data = encryptor.encrypt(
            eid = response.encryption.eid,
            iv = iv,
            aad = encryptor.generateAad(
                signature = signature,
                eid = response.encryption.eid,
            ),
            data = binaryResponse,
        ).getOrError { return it }

        val encryptedResponse = cbor.encodeEncryptedMessage(
            rawData = data,
            iv = iv,
            encryption = response.encryption
        ).getOrError { return it }

        return GcipResult.ok(
            GcipCoder.encodeBlock(
                header = header.copy(dataLength = encryptedResponse.size),
                data = encryptedResponse,
            )
        )
    }

    suspend fun retrieveRequest(blockData: ByteArray, caller: String?): GcipResult<SignerRequest> {
        val block = GcipCoder.decodeBlock(blockData)
            .getOrError { return it }

        val error = GcipResult.builder<SignerRequest>(block = block)

        if (!device.isDeviceSecure()) {
            return error.with(GcipStatus.UnsafeDevice)
        }

        val message = messageCoder.validateRequestMethod(block.method, block.data)
            .getOrCtx { return error.with(it) }

        val encryptionParams = message.getEncryptionMessageType()
            .getOrCtx { return error.with(it) }

        val (encryption, data) = when (encryptionParams) {
            is EncryptionMessageParams.Encrypted -> when (encryptionParams.encryption) {
                is Encryption.Handshake.Response -> return error.with(GcipStatus.InvalidFormat)
                is Encryption.Session -> {
                    val signature = GcipCoder.headerSignature(block.header)
                    encryptionParams.encryption to encryptor.decrypt(
                        nonce = block.nonce,
                        eid = encryptionParams.encryption.eid,
                        data = message.data,
                        iv = encryptionParams.iv,
                        aad = this@GcipSignerHandler.encryptor.generateAad(
                            signature = signature,
                            eid = encryptionParams.encryption.eid
                        ),
                    ).getOrCtx {
                        return error.with(it)
                    }
                }
            }
            is EncryptionMessageParams.Plain -> encryptionParams.encryption to message.data
        }

        val request = cbor.decodeSignerRequest(block.method, data)
            .getOrCtx { return error.with(it) }

        val blockId = hasher.sha256(blockData)
            .toUrlBase64Fmt()

        val signerBlock = block.toSignerBlock()
            .getOrCtx { return error.with(it) }

        val result = when (encryption) {
            is Encryption.Session -> when (request) {
                is GcipConnectRequest -> {
                    validator.validate(signerBlock, request, encryption, caller)
                }
                is GcipExtendRequest -> {
                    validator.validate(signerBlock, request, encryption, caller)
                }
                is GcipSignRequest -> {
                    validator.validate(blockId, signerBlock, request, encryption, caller)
                }
                is GcipDisconnectRequest -> {
                    validator.validate(signerBlock, request, encryption, caller)
                }
                else -> return error.with(GcipStatus.InvalidFormat)
            }
            is Encryption.Handshake.Request -> when (request) {
                is GcipExchangeRequest -> {
                    validator.validate(signerBlock, request, encryption, caller)
                }
                is GcipConnectRequest -> {
                    validator.validate(signerBlock, request, encryption, caller)
                }
                else -> return error.with(GcipStatus.InvalidFormat)
            }
        }

        val validatedRequest = result
            .getOrCtx { return error.with(it) }

        return GcipResult.ok(validatedRequest)
    }
}
