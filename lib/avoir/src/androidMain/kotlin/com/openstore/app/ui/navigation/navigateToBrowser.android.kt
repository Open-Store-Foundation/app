package com.openstore.app.ui.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavHostController
import com.openstore.app.log.L
import androidx.core.net.toUri

actual fun NavHostController.navigateToBrowser(uri: String) {
    val launchBrowser = Intent(Intent.ACTION_VIEW, uri.toUri())
    launchBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(launchBrowser)
    } catch (ex: ActivityNotFoundException) {
        L.e(ex)
    }
}