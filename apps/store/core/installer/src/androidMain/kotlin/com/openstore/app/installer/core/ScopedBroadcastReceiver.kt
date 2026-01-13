package com.openstore.app.installer.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.openstore.app.log.L
import foundation.openstore.kitten.android.scopes.OwnedScope

abstract class ScopedBroadcastReceiver : BroadcastReceiver() {

    protected val scope = OwnedScope(
        owner = this,
        isActive = false
    )

    final override fun onReceive(context: Context, intent: Intent) {
        scope.create()
        try {
            onHandleReceive(context, intent)
        } catch (e: Throwable) {
            L.d(e)
            throw e
        } finally {
            scope.destroy()
        }
    }

    abstract fun onHandleReceive(context: Context, intent: Intent)
}
