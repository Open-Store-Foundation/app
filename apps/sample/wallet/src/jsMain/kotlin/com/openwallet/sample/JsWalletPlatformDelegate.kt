package com.openwallet.sample

import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.transport.GcipStatus

class JsWalletPlatformDelegate : WalletPlatformDelegate {
    override fun importWallet(derivations: List<Derivation>, onResult: (GcipStatus, CommonResponse?) -> Unit) {
        console.log("JsWalletPlatformDelegate: importWallet called with $derivations")
    }

    override fun sign(connectionId: String, credentialId: String, onResult: (GcipStatus, CommonResponse?) -> Unit) {
        console.log("JsWalletPlatformDelegate: sign called for connection $connectionId, credential $credentialId")
    }

    override fun disconnect(connectionId: String, onResult: (GcipStatus, CommonResponse?) -> Unit) {
        console.log("JsWalletPlatformDelegate: disconnect called for connection $connectionId")
    }

    override fun extend(connectionId: String, derivations: List<Derivation>, onResult: (GcipStatus, CommonResponse?) -> Unit) {
        console.log("JsWalletPlatformDelegate: extend called for connection $connectionId with $derivations")
    }
}
