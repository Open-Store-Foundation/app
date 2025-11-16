package com.openstore.app.installer.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import foundation.openstore.kitten.api.Scope
import kotlinx.coroutines.Runnable

abstract class ScopedBroadcastReceiver : BroadcastReceiver() {

    private var observer: Runnable? = null
    private var isActive = true

    protected val scope = object : Scope<BroadcastReceiver> {
        override fun isActive(): Boolean {
            return isActive
        }

        override fun owner(): Any? {
            return this@ScopedBroadcastReceiver
        }

        override fun register(provider: Runnable): Boolean {
            if (!isActive) {
                provider.run()
            } else {
                observer = provider
            }

            return true
        }
    }

    final override fun onReceive(context: Context, intent: Intent) {
        isActive = true
        onHandleReceive(context, intent)
        isActive = false
        observer?.run()
    }

    abstract fun onHandleReceive(context: Context, intent: Intent)
}