package com.openstore.app.core.root

import android.content.Context
import com.scottyab.rootbeer.RootBeer

actual class DeviceRootProvider(
    private val context: Context
) {

    private val rootBeer by lazy { RootBeer(context) }

    actual fun isRooted(): Boolean {
        return rootBeer.isRooted
    }
}
