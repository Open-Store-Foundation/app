package com.openwallet.sample.di

import com.openwallet.sample.MainViewModel
import foundation.openstore.kitten.api.Component

interface WalletComponent : Component {
    fun provideMainViewModel(): MainViewModel
}




