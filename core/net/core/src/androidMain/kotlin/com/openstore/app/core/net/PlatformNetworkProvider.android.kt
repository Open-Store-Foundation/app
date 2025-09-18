package com.openstore.app.core.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class PlatformNetworkProvider(
    private val context: Context,
) : NetworkProviderLauncher {

    private val connection by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val isInit = AtomicBoolean(false)
    private val network by lazy { NetworkCallback(connection) }

    override val stateFlow: StateFlow<NetworkStatus> get() = network.stateFlow

    override fun launch() {
        Log.d(TAG, "Registering network callback")
        try {
            if (isInit.compareAndSet(false, true)) {
                network.start()

                Log.d(TAG, "Listener successfully set")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    connection.registerDefaultNetworkCallback(network)
                } else {
                    connection.registerNetworkCallback(NetworkRequest.Builder().build(), network)
                }
            }
        } catch (ignored: Exception) {
            // Sometimes there is a SecurityException "package does not belong to n"
            Log.e(TAG, "Error wile register net listener", PackageDoesNotBelongException(ignored))
        }
    }

    override fun isConnected(): Boolean {
        val isConnected = network.hasNetwork()
        Log.d(TAG, "Android network connection check = $isConnected")
        return isConnected
    }

    override fun status(): NetworkStatus {
        return stateFlow.value.apply {
            Log.d(TAG, "AndroidNetworkManager reporting status = ${this.javaClass.simpleName}")
        }
    }

    private class NetworkCallback(
        private val connection: ConnectivityManager,
    ) : ConnectivityManager.NetworkCallback() {

        private val isNetworkConnected: MutableStateFlow<NetworkStatus> = MutableStateFlow(NetworkStatus.Connecting)
        val stateFlow: StateFlow<NetworkStatus> = isNetworkConnected

        fun start() {
            isNetworkConnected.tryEmit(status())
        }

        fun status(): NetworkStatus {
            return if (hasNetwork()) {
                NetworkStatus.Available
            } else {
                NetworkStatus.Connecting
            }
        }

        fun hasNetwork(): Boolean {
            return connection.activeNetwork != null
        }

        // Delegates
        override fun onAvailable(network: Network) {
            Log.d(TAG, "Delegating available status to listener")
            isNetworkConnected.tryEmit(NetworkStatus.Available)
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "Delegating lost status to listener")
            isNetworkConnected.tryEmit(NetworkStatus.Connecting)
        }
    }

    private companion object {
        private const val TAG = "AndroidNetworkManager"
    }
}
