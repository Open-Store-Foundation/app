package foundation.openstore.signer.app.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlin.time.Clock

class TimeGenerator {

    // Time without timezone offset (Logic preserved from original code)
    fun getZonelessTime(): Long {
        val now = Clock.System.now()
        val timeZone = TimeZone.Companion.currentSystemDefault()
        val offsetMillis = timeZone.offsetAt(now).totalSeconds * 1000L
        return now.toEpochMilliseconds() - offsetMillis
    }
}