package com.openwallet.sample.di

import android.app.Application
import com.openwallet.sample.MainViewModel
import com.openwallet.sample.WalletRepository
import com.russhwolf.settings.SharedPreferencesSettings
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten
import foundation.openstore.kitten.api.deps.depLazy

object WalletInjection {
    fun init(app: Application) {
        Kitten.init(
            registry = WalletComponentRegistry(app)
        ) {
            register(WalletInjector) { walletCmp }
        }
    }
}

class WalletComponentRegistry(
    private val app: Application
) : ComponentRegistry() {

    private val repository: WalletRepository by depLazy {
        WalletRepository(
            SharedPreferencesSettings(
                app.getSharedPreferences("wallet_prefs", Application.MODE_PRIVATE)
            )
        )
    }

    val walletCmp: WalletComponent by shared<WalletComponent> {
        object : WalletComponent {
            override fun provideMainViewModel(): MainViewModel {
                return MainViewModel(
                    repository,
                )
            }
        }
    }
}





