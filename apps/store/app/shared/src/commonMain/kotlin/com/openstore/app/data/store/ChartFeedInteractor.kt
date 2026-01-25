package com.openstore.app.data.store

import com.openstore.app.core.async.Async
import com.openstore.app.core.net.NetworkProvider
import com.openstore.app.core.net.NetworkStatus
import com.openstore.app.data.Asset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch

enum class ChartFeedStatus {
    Syncing,
    Connecting,
    Ready,
}

interface ChartFeedInteractor {
    val observer: StateFlow<ChartFeedStatus>

    suspend fun loadChart(
        size: Int,
        offset: Int,
    ): Result<List<Asset>>
}

@OptIn(ExperimentalAtomicApi::class)
class ChartFeedInteractorDefault(
    private val objectRepo: ObjectRepo,
    private val networkProvider: NetworkProvider,
) : ChartFeedInteractor {

    private val isSync = AtomicInt(value = 0)

    private val state = MutableStateFlow(ChartFeedStatus.Syncing)
    override val observer: StateFlow<ChartFeedStatus> = state

    init {
        networkProvider.stateFlow
            .onEach { status ->
                updateStatus(status)
            }
            .launchIn(Async.globalScope())
    }

    override suspend fun loadChart(
        size: Int,
        offset: Int
    ): Result<List<Asset>> {
        if (offset == 0) {
            isSync.incrementAndFetch()
            updateStatus(networkProvider.status())
        }

        val result = objectRepo.loadChart(size, offset)

        if (offset == 0) {
            isSync.decrementAndFetch()
            updateStatus(networkProvider.status())
        }

        return result
    }

    private fun updateStatus(status: NetworkStatus) {
        state.tryEmit(mapStatus(status))
    }

    private fun mapStatus(status: NetworkStatus): ChartFeedStatus {
        if (status == NetworkStatus.Connecting) {
            return ChartFeedStatus.Connecting
        }

        if (isSync.load() > 0) {
            return ChartFeedStatus.Syncing
        }

        return ChartFeedStatus.Ready
    }
}
