package foundation.openstore.gcip.platform.data

import foundation.openstore.gcip.core.CallerData
import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.data.GcipPartiProvider
import foundation.openstore.gcip.core.util.GcipResult

expect class GcipPartiProviderPlatform : GcipPartiProvider {
    override fun prepareWalletInitialData(caller: String?): GcipResult<CallerData.Initial>
    override fun prepareWalletRawData(caller: String?): GcipResult<CallerData.Raw>
    override fun prepareWalletOrigin(
        suggestedOrigin: String,
        caller: String?
    ): GcipResult<String>

    override fun prepareSignerData(): GcipResult<SignerData>
}
