package org.openwallet.kitten.core

import foundation.openstore.kitten.api.SingleThread

//@SingleThread
//@ExperimentalNativeApi
//fun <Dep : Any> depRc(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepRc(initializer)

@SingleThread
fun <Dep : Any> depLazy(initializer: () -> Dep): LazyDep<Dep, out Any> = LazyDepGod(initializer)
