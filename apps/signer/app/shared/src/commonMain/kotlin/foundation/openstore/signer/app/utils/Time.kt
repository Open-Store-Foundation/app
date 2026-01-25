package foundation.openstore.signer.app.utils

import kotlin.time.Clock
import kotlin.time.Instant

fun currentTime(): Instant {
    return Clock.System.now()
}
