package foundation.openstore.gcip.core.util

import foundation.openstore.gcip.core.coder.GcipBlock
import foundation.openstore.gcip.core.transport.GcipStatus

data class GcipErrorContext(
    val error: GcipStatus,
    val e: Throwable? = null,
    val block: GcipBlock? = null
) {
    fun withEmptyBlock(provider: () -> GcipBlock?): GcipErrorContext {
        if (block == null) {
            return copy(block = provider())
        }

        return this
    }
}

// Immutable, Thread Safe implementation
sealed interface GcipResult<T> {
    companion object Companion {
        fun <T> ok(data: T): Data<T> {
            return Data(data)
        }

        fun ctx(error: GcipStatus, block: GcipBlock? = null, e: Throwable? = null): GcipErrorContext {
            return GcipErrorContext(error, e, block)
        }

        fun <T> err(error: GcipStatus, block: GcipBlock? = null, err: Throwable? = null): Error<T> {
            return Error(GcipErrorContext(error, err, block))
        }

        fun <T> builder(error: GcipStatus = GcipStatus.UnknownError, block: GcipBlock? = null, e: Throwable? = null): Error<T> {
            return Error(GcipErrorContext(error, e, block))
        }

        fun <T> err(ctx: GcipErrorContext): Error<T> {
            return Error(ctx = ctx)
        }
    }

    data class Data<T>(
        val data: T,
    ) : GcipResult<T>

    data class Error<T>(
        val ctx: GcipErrorContext,
    ) : GcipResult<T> {
        fun with(status: GcipStatus): Error<T> {
            return Error(ctx.copy(error = status))
        }

        fun with(block: GcipBlock): Error<T> {
            return Error(ctx.copy(block = block))
        }

        fun with(ctx: GcipErrorContext): Error<T> {
            return Error(
                ctx = ctx.copy(
                    error = ctx.error,
                    e = ctx.e ?: this.ctx.e,
                    block = ctx.block ?: this.ctx.block
                ),
            )
        }
    }
}

fun <T> GcipResult<T>.getOrNull(): T? {
    return when (this) {
        is GcipResult.Data -> data
        is GcipResult.Error -> null
    }
}

inline fun <T> GcipResult<T>.getOrCtx(handle: (GcipErrorContext) -> T): T {
    return when (this) {
        is GcipResult.Data -> data
        is GcipResult.Error -> handle(ctx)
    }
}

inline fun <T, reified D> GcipResult<T>.getOrError(handle: (GcipResult.Error<D>) -> T): T {
    return when (this) {
        is GcipResult.Data -> data
        is GcipResult.Error -> handle(GcipResult.err(ctx))
    }
}
