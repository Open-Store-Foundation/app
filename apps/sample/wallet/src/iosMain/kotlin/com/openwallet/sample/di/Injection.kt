package com.openwallet.sample.di

import com.openwallet.sample.MainViewModel
import com.openwallet.sample.WalletRepository
import com.russhwolf.settings.NSUserDefaultsSettings
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten
import foundation.openstore.kitten.api.deps.depLazy
import platform.Foundation.NSUserDefaults

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
        WalletRepository(NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults))
    }

    val walletCmp: WalletComponent by shared<WalletComponent> {
            object : WalletComponent {
                override fun provideMainViewModel(): MainViewModel {
                    return MainViewModel(repository)
                }
            }
        }
}





