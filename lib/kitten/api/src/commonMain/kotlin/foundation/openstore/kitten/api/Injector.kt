package foundation.openstore.kitten.api

open class Injector<Delegate : Any> {
    private lateinit var delegate: Function0<Delegate>
    private lateinit var borrower: LifecycleBorrower

    @SingleThread
    fun init(delegate: Function0<Delegate>, borrower: LifecycleBorrower) {
        this.delegate = delegate
        this.borrower = borrower
    }

    @SingleThread
    fun isInit(): Boolean {
        return ::delegate.isInitialized && ::borrower.isInitialized
    }

    @SingleThread
    fun <Subject> injectWith(owner: Scope<out Any>, factory: Delegate.() -> Subject): Subject {
        return borrower.borrow(owner) {
            factory.invoke(delegate.invoke())
        }
    }
}
