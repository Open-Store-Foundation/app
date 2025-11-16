package org.openwallet.kitten.core

import foundation.openstore.kitten.api.SingleThread
import kotlin.reflect.KProperty

abstract class LazyDep<Dep : Any, Holder : Any>(
    private val initializer: () -> Dep,
) {

    protected var memory: Holder? = null

    @SingleThread
    abstract fun getCachedDependency(): Dep?

    @SingleThread
    abstract fun createDependency(dep: Dep): Holder

    @SingleThread
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Dep {
        val cachedValue = getCachedDependency()
        if (cachedValue != null) {
            return cachedValue
        }

        val dep = initializer.invoke()
        memory = createDependency(dep)
        return dep
    }
}

class LazyDepGod<Dep : Any>(
    initializer: () -> Dep
) : LazyDep<Dep, Dep>(initializer) {
    override fun getCachedDependency(): Dep? = memory
    override fun createDependency(dep: Dep): Dep = dep
}

// TODO barely use LazyDepRc, don't want to keep external dep to opensavvy right now, will return if JB will implement it in stdlib
//import opensavvy.pedestal.weak.ExperimentalWeakApi
//import opensavvy.pedestal.weak.WeakRef
//@ExperimentalNativeApi
//@OptIn(ExperimentalWeakApi::class)
//class LazyDepRc<Dep : Any>(
//    initializer: () -> Dep
//) : LazyDep<Dep, WeakRef<Dep>>(initializer) {
//    override fun getCachedDependency(): Dep? = memory?.read()
//    override fun createDependency(dep: Dep): WeakRef<Dep> = WeakRef(dep)
//}
