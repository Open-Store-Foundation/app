package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.CallerData
import foundation.openstore.gcip.core.util.GcipResult

import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.toScheme
import foundation.openstore.gcip.core.transport.GcipPlatform

actual class GcipPartiProviderPlatform(
    private val name: String,
    private val bundleId: String,
    private val device: GcipPlatform,
) : GcipPartiProvider {

    actual override fun prepareWalletInitialData(caller: String?): GcipResult<CallerData.Initial> {
        return GcipResult.ok(CallerData.Initial(null, null, null))
    }

    actual override fun prepareWalletRawData(caller: String?): GcipResult<CallerData.Raw> {
        return GcipResult.ok(CallerData.Raw(null, null, null))
    }

    actual override fun prepareWalletOrigin(suggestedOrigin: String, caller: String?): GcipResult<String> {
        return GcipResult.ok(suggestedOrigin)
    }

    actual override fun prepareSignerData(): GcipResult<SignerData> {
        return GcipResult.ok(
            SignerData(
                name,
                device.toScheme(),
                bundleId
            )
        )
    }
}