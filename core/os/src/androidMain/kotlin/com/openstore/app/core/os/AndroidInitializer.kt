package com.openstore.app.core.os

import android.content.Context
import androidx.startup.Initializer

class AndroidInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Android.init(context)
    }

    override fun dependencies() = emptyList<Nothing>()
}
