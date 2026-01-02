package com.openwallet.sample

import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.transport.GcipStatus

interface WalletPlatformDelegate {
    fun importWallet(derivations: List<Derivation>, onResult: (GcipStatus, CommonResponse?) -> Unit)
    fun sign(connectionId: String, credentialId: String, onResult: (GcipStatus, CommonResponse?) -> Unit)
    fun disconnect(connectionId: String, onResult: (GcipStatus, CommonResponse?) -> Unit)
    fun extend(connectionId: String, derivations: List<Derivation>, onResult: (GcipStatus, CommonResponse?) -> Unit)
}
