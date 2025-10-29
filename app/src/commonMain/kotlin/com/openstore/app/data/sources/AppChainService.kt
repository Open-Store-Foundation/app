package com.openstore.app.data.sources

import com.openstore.app.core.common.toInt256Hex0x
import com.openstore.app.core.common.toNotLeadingHex0x
import com.openstore.app.core.net.TimedCacheMemory
import com.openstore.app.core.net.getOrLoad
import com.openstore.app.core.net.json_rpc.JsonRpcClient
import com.openstore.app.core.net.json_rpc.JsonRpcRequest
import com.openstore.app.data.Artifact
import com.openstore.app.data.Asset
import com.openstore.app.data.CategoryId
import com.openstore.app.data.TrackId
import com.openstore.app.data.decoder.AbiDecoder
import com.openstore.app.data.decoder.AbiEncoder
import com.openstore.app.data.decoder.AppAndOwnershipVersion
import com.openstore.app.data.decoder.AppBuild
import com.openstore.app.data.decoder.AppGeneralInfo
import com.openstore.app.data.decoder.AppOwnerPluginV1Version
import com.openstore.app.data.decoder.AppOwnershipProofsInfo
import com.openstore.app.data.decoder.AssetOwnershipStatus
import com.openstore.app.json.contentOrNull
import io.ktor.http.URLBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.decodeFromJsonElement

data class AssetWithArtifact( // TODO use one with FetchingRequest
    val obj: Asset,
    val artifact: Artifact? = null,
)

@Serializable
data class EthLog(
    val address: String,
    val topics: List<String>,
    val data: String,
    val blockNumber: String? = null,
    val transactionHash: String? = null,
    val transactionIndex: String? = null,
    val blockHash: String? = null,
    val logIndex: String? = null,
    val removed: Boolean? = null,
)

interface AppChainService {
    // Asset
    suspend fun findAssetGeneralInfo(asset: String): AppGeneralInfo?
    suspend fun findAsset(asset: String): Result<Asset?>
    suspend fun findAssetAndBuildData(asset: String): Result<AssetWithArtifact?>

    suspend fun getBuildInfo(asset: String, version: Long): AppBuild?

    suspend fun getOwnershipInfo(asset: String, version: Long): AppOwnerPluginV1Version?
    suspend fun getOwnershipProofsInfo(asset: String, blockNumber: Long, ownerVersion: Long): AppOwnershipProofsInfo?
    suspend fun getOwnershipVerificationStatus(asset: String, version: Long): AssetOwnershipStatus?

    suspend fun getLastVersionInStore(asset: String, trackId: TrackId = TrackId.RELEASE): Long?
    suspend fun getLastAppAndOwnershipVersion(asset: String, trackId: TrackId = TrackId.RELEASE): AppAndOwnershipVersion?
    suspend fun getVerifiedOwnershipVersion(asset: String, versionCode: Long): Long?

    suspend fun getArtifactLink(asset: Asset, artifact: Artifact): String?
    suspend fun getArtifactSources(asset: String, artifact: Artifact): List<String>
}

class AppChainServiceEvm(
    private val storeAddress: String,
    private val oracleAddress: String,
    private val rpcClient: JsonRpcClient,
    private val greenfieldClient: GreenfieldClient
) : AppChainService {

    companion object {
        private const val CALL_METHOD = "eth_call"
        private const val LOG_METHOD = "eth_getLogs"

        private const val APP_OWNERSHIP_DATA_CHANGED_EVENT_TOPIC = "0xca22805940de66b905c3b342d6e0cd1e9b955f0addec8d23272bdfe76eecf7fb"
    }

    private val generalTimedCache = TimedCacheMemory<AppGeneralInfo?>()
    private val objectDataTimedCache = TimedCacheMemory<AssetWithArtifact?>()
    private val versionTimedCache = TimedCacheMemory<Long?>()
    private val ownerVersionTimedCache = TimedCacheMemory<Long?>()
    private val ownerAndAppVersionTimedCache = TimedCacheMemory<AppAndOwnershipVersion?>()
    private val ownershipVerificationStatus = TimedCacheMemory<AssetOwnershipStatus?>()
    private val ownerTimedCache = TimedCacheMemory<AppOwnerPluginV1Version?>()

    override suspend fun findAsset(asset: String): Result<Asset?> {
        return runCatching {
            internalFindObject(asset)?.first
        }
    }

    override suspend fun findAssetAndBuildData(asset: String): Result<AssetWithArtifact?> {
        return runCatching {
            objectDataTimedCache.getOrLoad(asset) {
                internalFindObjectData(asset)
            }
        }
    }

    private suspend fun internalFindObjectData(objAddress: String): AssetWithArtifact? {
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

        return AssetWithArtifact(
            artifact = artifact,
            obj = obj.copy(logo = logo),
        )
    }

    private suspend fun internalFindObject(objAddress: String): Pair<Asset, Long?>? {
        val generalData = findAssetGeneralInfo(objAddress)
            ?: return null

        val version = getLastAppAndOwnershipVersion(objAddress)

        val ownerData = when (version) {
            null -> null
            else -> getOwnershipInfo(objAddress, version.ownership)
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
            isOracleVerified = version?.ownership != null && version.ownership > 0,
            isBuildVerified = true,

            isOsVerified = false,
            isHidden = false,

            rating = 0f,
            price = 0,
            downloads = 0,
        )

        return obj to version?.app
    }

    override suspend fun findAssetGeneralInfo(asset: String): AppGeneralInfo? {
        val cache = generalTimedCache.getOrLoad(asset) {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(asset, AbiEncoder.encodeGetGeneralInfo())
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

    override suspend fun getArtifactLink(asset: Asset, artifact: Artifact): String? {
        val meta = greenfieldClient.getObjectMeta(artifact.refId)
        val sp = greenfieldClient.getSpProvider(meta.virtualGroup.primarySpId)
        val link = greenfieldClient.getSafeAndroidBuildLink(
            sp.storageProvider,
            meta.objectInfo,
            asset,
            artifact
        )
        return link
    }

    override suspend fun getBuildInfo(asset: String, version: Long): AppBuild? {
        val request = JsonRpcRequest(
            method = CALL_METHOD,
            params = buildEvmJsonRpsParams(asset, AbiEncoder.encodeGetBuild(version))
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

    // TODO get ownership domain
    override suspend fun getOwnershipInfo(
        asset: String,
        version: Long
    ): AppOwnerPluginV1Version? {
        return ownerTimedCache.getOrLoad("$asset-$version") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(asset, AbiEncoder.encodeGetState(version.toULong()))
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

    override suspend fun getLastVersionInStore(asset: String, trackId: TrackId): Long? {
        return versionTimedCache.getOrLoad("$asset-${trackId.id}") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(
                    storeAddress, AbiEncoder.encodeGetLastObjVersion(asset, trackId.id)
                )
            )

            val buildVersion = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeSingleLong(it) }
            }

            if (buildVersion == 0L) {
                return@getOrLoad null
            }

            buildVersion
        }
    }

    override suspend fun getVerifiedOwnershipVersion(asset: String, versionCode: Long): Long? {
        return ownerVersionTimedCache.getOrLoad("$asset-$versionCode") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(
                    storeAddress, AbiEncoder.encodeGetOwnershipVersion(asset, versionCode)
                )
            )

            val ownershipVersion = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeSingleLong(it) }
            }

            if (ownershipVersion == 0L) {
                return@getOrLoad null
            }

            ownershipVersion
        }
    }

    override suspend fun getLastAppAndOwnershipVersion(asset: String, trackId: TrackId): AppAndOwnershipVersion? {
        return ownerAndAppVersionTimedCache.getOrLoad("$asset-${trackId.id}") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(
                    oracleAddress, AbiEncoder.getLastVersionWithOwnership(asset, trackId.id)
                )
            )

            val ownershipVersion = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeAppAndOwnershipVersion(it) }
            }

            if (ownershipVersion == null || ownershipVersion.app == 0L) {
                return@getOrLoad null
            }

            ownershipVersion
        }
    }

    override suspend fun getOwnershipProofsInfo(
        asset: String,
        blockNumber: Long,
        ownerVersion: Long,
    ): AppOwnershipProofsInfo? {
        val logs = getEthLogs(
            fromBlock = blockNumber.toNotLeadingHex0x(),
            toBlock = blockNumber.toNotLeadingHex0x(),
            address = asset,
            topic0 = APP_OWNERSHIP_DATA_CHANGED_EVENT_TOPIC,
            topic1 = ownerVersion.toInt256Hex0x()
        )

        val log = logs.firstOrNull()
            ?: return null

        val proofs = AbiDecoder.decodeAppOwnerChanged(log.topics, log.data)
        if (proofs.version != ownerVersion) {
            return null
        }

        return proofs
    }

    // TODO map to enum
    override suspend fun getOwnershipVerificationStatus(asset: String, version: Long): AssetOwnershipStatus? {
        return ownershipVerificationStatus.getOrLoad("$asset-${version}") {
            val request = JsonRpcRequest(
                method = CALL_METHOD,
                params = buildEvmJsonRpsParams(
                    oracleAddress, AbiEncoder.getOwnershipVerificationStatus(asset, version)
                )
            )

            val status = rpcClient.send(request) { response ->
                response.contentOrNull
                    ?.let { AbiDecoder.decodeAssetOwnershipStatus(it) }
            }

            status
        }
    }

    override suspend fun getArtifactSources(asset: String, artifact: Artifact): List<String> {
        val request = JsonRpcRequest(
            method = CALL_METHOD,
            params = buildEvmJsonRpsParams(asset, AbiEncoder.encodeGetDistribution())
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

    private suspend fun getEthLogs(
        fromBlock: String?,
        toBlock: String?,
        address: String?,
        topic0: String?,
        topic1: String?
    ): List<EthLog> {
        val request = JsonRpcRequest(
            method = LOG_METHOD,
            params = buildEthGetLogsParams(
                fromBlock = fromBlock,
                toBlock = toBlock,
                address = address,
                topic0 = topic0,
                topic1 = topic1,
            )
        )

        return rpcClient.send(request) { result ->
            val array = result as? JsonArray ?: return@send emptyList()
            array.mapNotNull { el ->
                runCatching { Json.decodeFromJsonElement<EthLog>(el) }
                    .getOrNull()
            }
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

    private fun buildEthGetLogsParams(
        fromBlock: String?,
        toBlock: String?,
        address: String?,
        topic0: String?,
        topic1: String?,
    ): JsonArray {
        return buildJsonArray {
            addJsonObject {
                if (fromBlock != null) put("fromBlock", JsonPrimitive(fromBlock))
                if (toBlock != null) put("toBlock", JsonPrimitive(toBlock))
                if (address != null) put("address", JsonPrimitive(address))

                if (topic0 != null || topic1 != null) {
                    put("topics", buildJsonArray {
                        add(topic0?.let { JsonPrimitive(it) } ?: JsonNull)
                        add(topic1?.let { JsonPrimitive(it) } ?: JsonNull)
                    })
                }
            }
        }
    }
}

