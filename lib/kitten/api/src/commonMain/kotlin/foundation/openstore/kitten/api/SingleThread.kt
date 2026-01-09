package foundation.openstore.kitten.api

/**
 * Annotates a class, function, or property getter that must be accessed from a single thread.
 *
 * This annotation serves as documentation to indicate that the marked element is not thread-safe
 * and should be used with caution in concurrent environments. It does not enforce thread confinement
 * at runtime but acts as a warning for developers.
 */
@Target(
    allowedTargets = [
        AnnotationTarget.CLASS,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER
    ]
)
@Retention(value = AnnotationRetention.SOURCE)
annotation class SingleThread
