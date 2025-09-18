package com.openstore.app.data.sources

import com.openstore.app.core.net.TimedCacheMemory
import com.openstore.app.core.net.getOrLoad
import com.openstore.app.core.net.json_rpc.JsonRpcClient
import com.openstore.app.core.net.json_rpc.JsonRpcRequest
import com.openstore.app.data.Artifact
import com.openstore.app.data.CategoryId
import com.openstore.app.data.Asset
import com.openstore.app.data.TrackId
import com.openstore.app.data.decoder.AbiDecoder
import com.openstore.app.data.decoder.AbiEncoder
import com.openstore.app.data.decoder.AppBuild
import com.openstore.app.data.decoder.AppGeneralInfo
import com.openstore.app.data.decoder.AppOwnerPluginV1Version
import com.openstore.app.json.contentOrNull
import io.ktor.http.URLBuilder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray

data class OnChainObject(
    val obj: Asset,
    val artifact: Artifact? = null,
)

interface AppChainService {
    suspend fun collectObjectData(objAddress: String): Result<OnChainObject?>
    suspend fun findObject(objAddress: String): Result<Asset?>

    suspend fun findGeneralInfo(objAddress: String): AppGeneralInfo?

    suspend fun getBuildInfo(objAddress: String, version: Long): AppBuild?
    suspend fun getOwnershipInfo(objAddress: String, version: Long): AppOwnerPluginV1Version?

    suspend fun getLastVersionInStore(objAddress: String, trackId: TrackId = TrackId.RELEASE): Long?
    suspend fun getVerifiedOwnershipVersion(objAddress: String, buildId: Long): Long?

    suspend fun getArtifactLink(obj: Asset, artifact: Artifact): String?
    suspend fun getArtifactSources(objAddress: String, artifact: Artifact): List<String>
}

class AppChainServiceEvm(
    private val storeAddress: String,
    private val rpcClient: JsonRpcClient,
    private val greenfieldClient: GreenfieldClient
) : AppChainService {

    companion object {
        private const val CALL_METHOD = "eth_call"
    }

    private val generalTimedCache = TimedCacheMemory<AppGeneralInfo?>()
    private val objectDataTimedCache = TimedCacheMemory<OnChainObject?>()
    private val versionTimedCache = TimedCacheMemory<Long?>()
    private val ownerVersionTimedCache = TimedCacheMemory<Long?>()
    private val ownerTimedCache = TimedCacheMemory<AppOwnerPluginV1Version?>()

    override suspend fun findObject(objAddress: String): Result<Asset?> {
        return runCatching {
            internalFindObject(objAddress)?.first
        }
    }

    override suspend fun collectObjectData(objAddress: String): Result<OnChainObject?> {
        return runCatching {
            objectDataTimedCache.getOrLoad(objAddress) {
                internalFindObjectData(objAddress)
            }
        }
    }

    private suspend fun internalFindObjectData(objAddress: String): OnChainObject? {
        val (obj, buildVersion) = internalFindObject(objAddress)
            ?: return null

        val buildInfo = buildVersion?.let { getBuildInfo(objAddress, it) }
        val meta = buildInfo?.let { greenfieldClient.getObjectMeta(it.referenceId) }

        val artifact = buildInfo?.let {
            meta?.let {
                Artifact(
                    id = -1,
                    refId = buildInfo.referenceId,
                    checksum = buildInfo.checksum,
                    versionCode = buildInfo.versionCode,
                    versionName = buildInfo.versionName,
                    size = meta.objectInfo.size,
                    protocolId = buildInfo.protocolId,
                )
            }
        }

        val sp = meta?.let { greenfieldClient.getSpProvider(meta.virtualGroup.primarySpId) }
        val logo = if (sp != null) {
            greenfieldClient.getSafeAppLogo(
                endpoint = sp.storageProvider.endpoint,
                bucketName = meta.objectInfo.bucketName,
                packageName = obj.packageName
            )
        } else {
            null
        }

        return OnChainObject(
            artifact = artifact,
            obj = obj.copy(logo = logo),
        )
    }

    private suspend fun internalFindObject(objAddress: String): Pair<Asset, Long?>? {
        val generalData = findGeneralInfo(objAddress)
            ?: return null

        val buildVersion = getLastVersionInStore(objAddress, TrackId.RELEASE)
        val ownerVersion = buildVersion?.let { lastVersion -> getVerifiedOwnershipVersion(objAddress, lastVersion) }

        val ownerData = when (ownerVersion) {
            null -> null
            else -> getOwnershipInfo(objAddress, ownerVersion)
        }

        val obj = Asset(
            id = -1,
            address = objAddress,
            name = generalData.name,
            packageName = generalData.packageStr,
            website = ownerData?.domain,
            logo = null, // TODO get logo from chain
            description = generalData.description,
            categoryId = generalData.categoryId,
            platformId = generalData.platformId,
            typeId = CategoryId.requireById(generalData.categoryId).objectTypeId.id,
            isOracleVerified = ownerVersion != null && ownerVersion > 0,
            isBuildVerified = ownerVersion != null && ownerVersion > 0,

            isOsVerified = false,
            isHidden = false,

            rating = 0f,
            price = 0,
            downloads = 0,
        )

        return obj to buildVersion
    }

    override suspend fun findGeneralInfo(objAddress: String): AppGeneralInfo? {
        val cache = generalTimedCache.getOrLoad(objAddress) {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(objAddress, AbiEncoder.encodeGetGeneralInfo())
            )

            val generalInfo = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeGeneralInfo(it) }
            }

            if (generalInfo?.name.isNullOrBlank()) {
                return@getOrLoad null
            }

            generalInfo
        }

        return cache
    }

    override suspend fun getArtifactLink(obj: Asset, artifact: Artifact): String? {
        val meta = greenfieldClient.getObjectMeta(artifact.refId)
        val sp = greenfieldClient.getSpProvider(meta.virtualGroup.primarySpId)
        val link = greenfieldClient.getSafeAndroidBuildLink(
            sp.storageProvider,
            meta.objectInfo,
            obj,
            artifact
        )
        return link
    }

    override suspend fun getBuildInfo(objAddress: String, version: Long): AppBuild? {
        val request = JsonRpcRequest(
            method = CALL_METHOD,
            params = buildEvmJsonRpsParams(objAddress, AbiEncoder.encodeGetBuild(version))
        )

        val buildInfo = rpcClient.send(request) { response ->
            response.contentOrNull
                ?.let { AbiDecoder.decodeAppBuild(it) }
        }

        if (buildInfo?.referenceId.isNullOrBlank()) {
            return null
        }

        return buildInfo
    }

    override suspend fun getOwnershipInfo(
        objAddress: String,
        version: Long
    ): AppOwnerPluginV1Version? {
        return ownerTimedCache.getOrLoad("$objAddress-$version") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(objAddress, AbiEncoder.encodeGetState(version.toULong()))
            )

            val ownershipInfo = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeOwnershipInfo(it) }
            }

            if (ownershipInfo?.domain.isNullOrBlank()) {
                return@getOrLoad null
            }

            ownershipInfo
        }
    }

    override suspend fun getLastVersionInStore(objAddress: String, trackId: TrackId): Long? {
        return versionTimedCache.getOrLoad("$objAddress-${trackId.id}") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(
                    storeAddress, AbiEncoder.encodeGetLastObjVersion(objAddress, trackId.id)
                )
            )

            val buildVersion = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeLongVersion(it) }
            }

            if (buildVersion == 0L) {
                return@getOrLoad null
            }

            buildVersion
        }
    }

    override suspend fun getVerifiedOwnershipVersion(objAddress: String, buildId: Long): Long? {
        return ownerVersionTimedCache.getOrLoad("$objAddress-$buildId") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(
                    storeAddress, AbiEncoder.encodeGetOwnershipVersion(objAddress, buildId)
                )
            )

            val ownershipVersion = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeLongVersion(it) }
            }

            if (ownershipVersion == 0L) {
                return@getOrLoad null
            }

            ownershipVersion
        }
    }

    override suspend fun getArtifactSources(objAddress: String, artifact: Artifact): List<String> {
        val request = JsonRpcRequest(
            method = CALL_METHOD,
            params = buildEvmJsonRpsParams(objAddress, AbiEncoder.encodeGetDistribution())
        )

        val distributionData = rpcClient.send(request) { response ->
            response.contentOrNull
                ?.let { AbiDecoder.decodeDistributionSources(it) }
        }

        if (distributionData?.sources.isNullOrEmpty()) {
            return emptyList()
        }

        return distributionData.sources
            .map { link ->
                link
                    .replace("\${VERSION_CODE}", artifact.versionCode.toString())
                    .replace("\${VERSION_NAME}", artifact.versionName.orEmpty())
                    .replace("\${REF_ID}", artifact.refId)
                    .replace("\${CHECKSUM}", artifact.checksum)
            }
            .filter { link ->
                runCatching { URLBuilder(link) }
                    .isSuccess
            }
    }

    fun buildEvmJsonRpsParams(to: String, data: String): JsonArray {
        return buildJsonArray {
            addJsonObject {
                put("to", JsonPrimitive(to))
                put("data", JsonPrimitive(data))
            }

            add(JsonPrimitive("latest"))
        }
    }
}

