package com.openwallet.sample.di

import android.app.Application
import com.openwallet.sample.MainViewModel
import com.openwallet.sample.WalletRepository
import com.russhwolf.settings.SharedPreferencesSettings
import org.openwallet.kitten.core.ComponentProvider
import org.openwallet.kitten.core.Kitten
import org.openwallet.kitten.core.depLazy

object WalletInjection {
    fun init(app: Application) {
        Kitten.init(
            provider = WalletComponentProvider(app)
        ) { deps ->
            register(WalletInjector) { deps.walletCmp }
        }
    }
}

class WalletComponentProvider(
    private val app: Application
) : ComponentProvider() {

    private val repository: WalletRepository by depLazy {
        WalletRepository(
            SharedPreferencesSettings(
                app.getSharedPreferences("wallet_prefs", Application.MODE_PRIVATE)
            )
        )
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




