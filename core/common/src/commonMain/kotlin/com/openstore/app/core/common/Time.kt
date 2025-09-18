package com.openstore.app.core.common

import kotlin.time.Clock

object Time {
    fun nowMs(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}