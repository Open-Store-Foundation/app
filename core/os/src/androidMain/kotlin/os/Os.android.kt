package com.openstore.app.core.os

actual object Os {
    actual fun platform(): Platform {
        return Platform.Android
    }
}