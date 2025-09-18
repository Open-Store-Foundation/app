package com.openstore.app.data.sources

import com.openstore.app.core.net.TimedCacheMemory
import com.openstore.app.core.net.getOrLoad
import com.openstore.app.core.net.json_rpc.util.bodyOrError
import com.openstore.app.data.Artifact
import com.openstore.app.data.Asset
import com.openstore.app.log.L
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ObjectMeta(
    @SerialName("global_virtual_group")
    val virtualGroup: VirtualGroup,
    @SerialName("object_info")
    val objectInfo: ObjectInfo,
)

@Serializable
data class ObjectInfo(
    @SerialName("bucket_name")
    private val bucket: String,
    @SerialName("object_name")
    val objectName: String,
    @SerialName("payload_size")
    val size: Long,
) {
    @Transient
    val bucketName = bucket.lowercase()
}

@Serializable
data class VirtualGroup(
    @SerialName("primary_sp_id")
    val primarySpId: Int
)

@Serializable
data class SpProviderResponse(
    val storageProvider: SpProviderInfo
)

@Serializable
data class SpProviderInfo(
    val endpoint: String,
    val status: String,
)

interface GreenfieldClient {
    suspend fun getObjectMeta(objectRef: String): ObjectMeta
    suspend fun getSpProvider(id: Int): SpProviderResponse

    fun getSafeAppLogo(
        endpoint: String,
        bucketName: String,
        packageName: String,
    ): String

    fun getSafeAndroidBuildLink(
        sp: SpProviderInfo,
        info: ObjectInfo,
        obj: Asset,
        artifact: Artifact,
    ): String?
}

class GreenfieldClientHttp(
    private val host: String,
    private val client: HttpClient
) : GreenfieldClient {

    private val metaTimedCache = TimedCacheMemory<ObjectMeta>()
    private val spTimedCache = TimedCacheMemory<SpProviderResponse>()

    override suspend fun getObjectMeta(objectRef: String): ObjectMeta {
        return metaTimedCache.getOrLoad(objectRef) {
            val result = client.get(host) {
                url {
                    appendPathSegments("/greenfield/storage/head_object_by_id/${objectRef}")
                }
            }

            result.bodyOrError()
        }
    }

    override suspend fun getSpProvider(id: Int): SpProviderResponse {
        return spTimedCache.getOrLoad(id.toString()) {
            val result = client.get(host) {
                url {
                    appendPathSegments("/greenfield/storage_provider/$id")
                }
            }

            result.bodyOrError()
        }
    }

    override fun getSafeAppLogo(
        endpoint: String,
        bucketName: String,
        packageName: String,
    ): String {
        val builder = URLBuilder(endpoint)
        builder.path("/view/${bucketName}/open-store-external/${packageName.replace(".", "_")}/logo.png")
        val actualLink = builder.build().toString()
        return actualLink
    }

    override fun getSafeAndroidBuildLink(
        sp: SpProviderInfo,
        info: ObjectInfo,
        obj: Asset,
        artifact: Artifact,
    ): String? {
        val builder = URLBuilder(sp.endpoint)
        val primaryHost = builder.host

        builder.host = "${info.bucketName}.${primaryHost}"
        builder.path(info.objectName)
        val supposedLink = builder.build().toString()

        builder.host = "${info.bucketName}.${primaryHost}" // TODO check bucketName
        builder.path(buildGfArtifactPath(obj.packageName, artifact))
        val actualLink = builder.build().toString()

        if (supposedLink != actualLink) {
            L.e("Illegal link: supposedLink: $supposedLink, actualLink: $actualLink")
            return null
        }

        return actualLink
    }

    private fun buildGfArtifactPath(packageName: String, artifact: Artifact): String {
        return "open-store-external/${packageName.replace(".", "_")}/v/${artifact.versionName}/${artifact.versionCode}/${artifact.checksum}.apk"
    }
}