package foundation.openstore.gcip.core.util

import foundation.openstore.gcip.core.CallerData

// TODO use
class GcipOriginComparator {

    fun isCallerBelongsTo(data: CallerData.Initial, callerData: CallerData.Raw): Boolean {
        if (data.scheme?.value != callerData.scheme?.value) {
            return false
        }

        if (data.id != callerData.id) {
            return false
        }

        val signs = callerData.signatures
        if (!signs.isNullOrEmpty()) {
            val signSet = signs.toSet()
            val fingerprint = data.signature
            if (!signSet.contains(fingerprint)) {
                return false
            }
        }

        return true
    }
}