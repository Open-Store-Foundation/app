//package org.openwallet.kitten.core
//
//import foundation.openstore.kitten.api.Injector
//import foundation.openstore.kitten.api.SingleThread
//
//
//interface ComponentLifecycle {
//
//	@SingleThread
//	fun <Delegate : Any, Subject> Injector<Delegate>.inject(factory: Delegate.() -> Subject): Subject {
//		return injectWith(this, factory)
//	}
//}
