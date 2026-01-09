package foundation.openstore.kitten.api

/**
 * Base class for all injectors in the Kitten Dependency Injection library.
 *
 * An Injector is responsible for providing access to components and dependencies within a specific
 * module or scope. It facilitates the "inject" or "borrow" mechanism where dependencies are
 * temporarily accessed from a component owner.
 *
 * @param Delegate The type of the component or delegate that this injector manages.
 */
open class Injector<Delegate : Any> {
    private lateinit var delegate: Function0<Delegate>
    private lateinit var borrower: LifecycleBorrower

    /**
     * Initializes the injector with a delegate provider and a lifecycle borrower.
     *
     * This method must be called before any injection can take place.
     *
     * @param delegate A provider function that returns the delegate (component) instance.
     * @param borrower The [LifecycleBorrower] responsible for managing the lifecycle and thread safety of the injection process.
     */
    @SingleThread
    fun init(delegate: Function0<Delegate>, borrower: LifecycleBorrower) {
        this.delegate = delegate
        this.borrower = borrower
    }

    /**
     * Checks if the injector has been initialized.
     *
     * @return `true` if initialized, `false` otherwise.
     */
    @SingleThread
    fun isInit(): Boolean {
        return ::delegate.isInitialized && ::borrower.isInitialized
    }

    /**
     * Injects dependencies into a context defined by the [owner].
     *
     * This method allows temporary access to the delegate's dependencies within the provided block.
     * The lifecycle of the access is managed by the [LifecycleBorrower].
     *
     * @param Subject The type of the result produced by the factory block.
     * @param owner The scope owner requesting the injection.
     * @param factory A function block where the dependencies are accessed and used.
     * @return The result of the factory block.
     */
    @SingleThread
    fun <Subject> injectWith(owner: Scope<out Any>, factory: Delegate.() -> Subject): Subject {
        return borrower.borrow(owner) {
            factory.invoke(delegate.invoke())
        }
    }
}
