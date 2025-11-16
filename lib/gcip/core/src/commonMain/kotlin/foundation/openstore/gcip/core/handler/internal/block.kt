package foundation.openstore.gcip.core.handler.internal

import foundation.openstore.gcip.core.SignerBlock
import foundation.openstore.gcip.core.coder.GcipBlock
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipResult

internal fun GcipBlock.toSignerBlock(): GcipResult<SignerBlock> {
    return GcipResult.ok(
        SignerBlock(
            version = version,
            status = status,
            nonce = nonce,
            method = method ?: return GcipResult.err(GcipStatus.InvalidBlock),
            data = data ?: return GcipResult.err(GcipStatus.InvalidBlock),
        )
    )
}
