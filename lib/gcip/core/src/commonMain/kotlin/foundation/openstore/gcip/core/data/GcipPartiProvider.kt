package foundation.openstore.gcip.core.data

import foundation.openstore.gcip.core.SignerData
import foundation.openstore.gcip.core.CallerData
import foundation.openstore.gcip.core.util.GcipResult

interface GcipPartiProvider {
    fun prepareWalletInitialData(caller: String?): GcipResult<CallerData.Initial>
    fun prepareWalletRawData(caller: String?): GcipResult<CallerData.Raw>
    fun prepareWalletOrigin(suggestedOrigin: String, caller: String?): GcipResult<String>
    fun prepareSignerData(): GcipResult<SignerData>
}
