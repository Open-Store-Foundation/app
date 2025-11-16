package com.openstore.app.ui.workround

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.requireActivity(): Activity =
    findActivityOrNull() ?: error("Cannot find Activity")

fun Context.findActivityOrNull(): Activity? {
    var context = this
    while (context !is Activity) {
        if (context is ContextWrapper) {
            context = context.baseContext
        } else {
            return null
        }
    }
    return context
}
