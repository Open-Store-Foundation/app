package com.openwallet.sample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.openstore.app.ui.AvoirTheme
import com.openwallet.sample.di.WalletInjection
import foundation.openstore.gcip.platform.GcipDataBundle
import platform.UIKit.UIViewController

@Suppress("unused")
fun MainViewController(): UIViewController {
    WalletInjection.init()
    var vc: UIViewController? = null

    vc = ComposeUIViewController {
        val vcProvider: () -> UIViewController = { vc!! }

        val delegate = remember { IosWalletPlatformDelegate(vcProvider) }
        var responseData by remember { mutableStateOf<ByteArray?>(null) }

        AvoirTheme {
            WalletApp(
                responseData = responseData,
                onResponseConsumed = { responseData = null },
                startIntent = { bytes ->
                    delegate.startIntent(bytes) { result ->
                        responseData = result
                    }
                },
                onMessage = { msg ->
                    println(msg)
                },
            )
        }
    }

    return vc
}

