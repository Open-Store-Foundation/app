package com.openwallet.sample.di

import com.openwallet.sample.MainViewModel
import com.openwallet.sample.WalletRepository
import com.russhwolf.settings.StorageSettings
import foundation.openstore.gcip.ble.BleJsFactory
import kotlinx.browser.localStorage
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten
import foundation.openstore.kitten.api.deps.depLazy

object WalletInjection {
    fun init() {
        Kitten.init(
            registry = WalletComponentRegistry()
        ) {
            register(WalletInjector) { walletCmp }
        }
    }
}

class WalletComponentRegistry : ComponentRegistry() {

    private val repository: WalletRepository by depLazy {
        WalletRepository(
            StorageSettings(localStorage)
        )
    }

    val walletCmp: WalletComponent by shared<WalletComponent> {
        object : WalletComponent {
            override fun provideMainViewModel(): MainViewModel {
                return MainViewModel(
                    repository,
                    bleCentral = BleJsFactory.defaultBleProvider(),
                )
            }
        }
    }
}
