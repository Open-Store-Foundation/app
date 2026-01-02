package com.openwallet.sample

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.openstore.app.ui.AvoirTheme
import com.russhwolf.settings.StorageSettings
import org.jetbrains.skiko.wasm.onWasmReady
import kotlinx.browser.localStorage

import coil3.ImageLoader
import coil3.SingletonImageLoader

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .diskCache(null)
            .build()
    }

    onWasmReady {
        ComposeViewport {
            val scope = rememberCoroutineScope()
            // StorageSettings works with localStorage in JS
            val repository = remember { WalletRepository(StorageSettings(localStorage)) }
            val delegate = remember { JsWalletPlatformDelegate() }
            val controller = remember { WalletController(repository, delegate, scope) }
            
            val wallets by controller.wallets.collectAsState()

            AvoirTheme {
                WalletListScreen(
                    exchangeSessions = emptyList(),
                    wallets = wallets,
                    onImportWallet = controller::onImportWallet,
                    onRequestExchange = {},
                    onConnectExchangeSession = { _, _ -> },
                    onSign = controller::onSign,
                    onDisconnect = controller::onDisconnect,
                    onExtend = controller::onExtend
                )
            }
        }
    }
}
