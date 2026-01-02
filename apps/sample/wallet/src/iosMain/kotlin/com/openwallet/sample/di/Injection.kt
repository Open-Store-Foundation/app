package com.openwallet.sample.di

import com.openwallet.sample.MainViewModel
import com.openwallet.sample.WalletRepository
import com.russhwolf.settings.NSUserDefaultsSettings
import org.openwallet.kitten.core.ComponentProvider
import org.openwallet.kitten.core.Kitten
import org.openwallet.kitten.core.depLazy
import platform.Foundation.NSUserDefaults

object WalletInjection {
    fun init() {
        Kitten.init(
            provider = WalletComponentProvider()
        ) { deps ->
            register(WalletInjector) { deps.walletCmp }
        }
    }
}

class WalletComponentProvider : ComponentProvider() {

    private val repository: WalletRepository by depLazy {
        WalletRepository(NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults))
    }

    val walletCmp: WalletComponent
        get() = multiOwner {
            object : WalletComponent {
                override fun provideMainViewModel(): MainViewModel {
                    return MainViewModel(repository)
                }
            }
        }
}




