package com.openstore.app.data.node

import com.openstore.app.store.common.store.KeyValueStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

@Serializable
enum class CustomNodeType(val key: String) {
    BSC("bsc_node"),
    GREENFIELD("greenfield_node"),
    API("api_node");
}

data class AppNodes(
    val bscUrl: String? = null,
    val defaultBscUrl: String,

    val greenfieldUrl: String? = null,
    val defaultGreenfieldUrl: String,

    val apiUrl: String? = null,
    val defaultApiUrl: String,
) {
    val bscUrlValue: String get() = bscUrl ?: defaultBscUrl
    val greenfieldUrlValue: String get() = greenfieldUrl ?: defaultGreenfieldUrl
    val apiUrlValue: String get() = apiUrl ?: defaultApiUrl

    val isBscDefault: Boolean get() = bscUrl == null
    val isGreenfieldDefault: Boolean get() = greenfieldUrl == null
    val isApiDefault: Boolean get() = apiUrl == null
}

interface NodeRepo {
    suspend fun restoreAppNodes(): AppNodes
    suspend fun getAppNodes(): AppNodes

    suspend fun restoreNode(type: CustomNodeType): String?
    suspend fun getNode(type: CustomNodeType): String
    suspend fun setNode(type: CustomNodeType, url: String?)
}

class NodeRepoStorage(
    private val defaultNodes: AppNodes,
    private val storage: KeyValueStorage
) : NodeRepo {

    private val mutex = Mutex()
    private var nodes: AppNodes? = null

    override suspend fun restoreAppNodes(): AppNodes {
        val nodes = AppNodes(
            bscUrl = storage.getStringOrNull(CustomNodeType.BSC.key),
            defaultBscUrl = defaultNodes.defaultBscUrl,

            greenfieldUrl = storage.getStringOrNull(CustomNodeType.GREENFIELD.key),
            defaultGreenfieldUrl = defaultNodes.defaultGreenfieldUrl,

            apiUrl = storage.getStringOrNull(CustomNodeType.API.key),
            defaultApiUrl = defaultNodes.defaultApiUrl,
        )

        return nodes
    }

    override suspend fun getAppNodes(): AppNodes {
        if (nodes != null) {
            return nodes!!
        }

        return mutex.withLock {
            val restored = restoreAppNodes()
            nodes = restored

            restored
        }
    }

    override suspend fun setNode(type: CustomNodeType, url: String?) {
        if (url == null) {
            storage.remove(type.key)
        } else {
            storage.putString(type.key, url)
        }
    }

    override suspend fun restoreNode(type: CustomNodeType): String? {
        return when (type) {
            CustomNodeType.BSC -> storage.getStringOrNull(CustomNodeType.BSC.key)
            CustomNodeType.GREENFIELD -> storage.getStringOrNull(CustomNodeType.GREENFIELD.key)
            CustomNodeType.API -> storage.getStringOrNull(CustomNodeType.API.key)
        }
    }

    override suspend fun getNode(type: CustomNodeType): String {
        return getAppNodes().let {
            when (type) {
                CustomNodeType.BSC -> it.bscUrlValue
                CustomNodeType.GREENFIELD -> it.greenfieldUrlValue
                CustomNodeType.API -> it.apiUrlValue
            }
        }
    }
}
