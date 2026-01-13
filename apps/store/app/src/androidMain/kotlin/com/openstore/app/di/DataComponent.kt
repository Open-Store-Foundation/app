package com.openstore.app.di

import android.content.Context
import com.openstore.app.core.net.json_rpc.JsonRpcClient
import com.openstore.app.data.PlatformId
import com.openstore.app.data.artifact.ArtifactRepo
import com.openstore.app.data.artifact.ArtifactService
import com.openstore.app.data.artifact.ArtifactServiceDefault
import com.openstore.app.data.db.ResponseRepoKeyValue
import com.openstore.app.data.db.ResponseRepo
import com.openstore.app.data.installation.InstallationOnChainValidator
import com.openstore.app.data.installation.InstallationRepoDefault
import com.openstore.app.data.installation.InstallationRequestRepo
import com.openstore.app.data.installation.InstallationValidator
import com.openstore.app.data.profile.ProfileRepo
import com.openstore.app.data.report.ReportRepo
import com.openstore.app.data.report.ReportRepoDefault
import com.openstore.app.data.report.ReportService
import com.openstore.app.data.report.ReportServiceDefault
import com.openstore.app.data.review.ReviewService
import com.openstore.app.data.review.ReviewServiceDefault
import com.openstore.app.data.settings.SettingsRepo
import com.openstore.app.data.sources.GreenfieldClient
import com.openstore.app.data.sources.GreenfieldClientHttp
import com.openstore.app.data.sources.AppChainService
import com.openstore.app.data.sources.AppChainServiceEvm
import com.openstore.app.data.stat.StatService
import com.openstore.app.data.stat.StatServiceDefault
import com.openstore.app.data.store.ObjectRepo
import com.openstore.app.data.store.ObjectRepoDefault
import com.openstore.app.data.store.AssetService
import com.openstore.app.data.store.AssetServiceDefault
import com.openstore.app.data.store.StoreInteractor
import com.openstore.app.data.store.StoreInteractorDefault
import com.openstore.app.data.store.StoreService
import com.openstore.app.data.store.StoreServiceDefault
import com.openstore.app.data.update.AppUpdateInteractor
import com.openstore.app.data.update.AppUpdateInteractorDefault
import com.openstore.app.installer.ApkInstallationMetaRepo
import com.openstore.app.installer.InstallationEventProducer
import com.openstore.app.installer.InstallationMetaRepoStorage
import com.openstore.app.installer.InstallationRequestQueue
import com.openstore.app.installer.MutableInstallationMetaRepo
import foundation.openstore.core.crypto.NativeSignatureVerifier
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.deps.depLazy

interface DataComponent : Component {
    val artifactRepo: ArtifactRepo
    val artifactService: ArtifactService
    val appChainService: AppChainService

    val profileRepo: ProfileRepo
    val profileService: ProfileRepo

    val objectService: AssetService
    val objRepo: ObjectRepo

    val settingsRepo: SettingsRepo

    val appUpdateInteractor: AppUpdateInteractor
    val installationRequestRepo: InstallationRequestRepo
    val installationMetaRepo: MutableInstallationMetaRepo
    val installationQueue: InstallationRequestQueue
    val installationProvider: InstallationEventProducer

    val reportRepo: ReportRepo
    val reportService: ReportService

    val responseRepo: ResponseRepo
    val reviewService: ReviewService
    val statService: StatService

    val storeService: StoreService
    val storeInteractor: StoreInteractor
}

class DataComponentDefault(
    private val app: Context,
    private val platformId: PlatformId,
    private val caip2Chain: String,
    private val storeAddress: String,
    private val oracleAddress: String,
    private val netComponent: NetComponent,
    private val storageComponent: StorageComponent,
) : DataComponent {

    companion object {
        const val NODES_STORAGE = "nodes"
        const val REQUESTS_STORAGE = "requests"
        const val SETTINGS_STORAGE = "settings"
        const val APP_META_STORAGE = "app-meta"
    }

    //
    // Internal
    //
    private val nodeHttpClient by depLazy {
        netComponent.bscHttpClient()
    }

    private val greenfieldHttpClient by depLazy {
        netComponent.greenfieldHttpClient()
    }

    private val clientHttpClient by depLazy {
        netComponent.clientHttpClient()
    }

    private val statHttpClient by depLazy {
        netComponent.clientHttpClient()
    }

    private val nodeJsonRpc by depLazy {
        JsonRpcClient(
            netComponent.baseEthHost,
            nodeHttpClient,
        )
    }

    private val installationValidator: InstallationValidator by depLazy {
        InstallationOnChainValidator(
            chainCaip2 = caip2Chain,
            verifier = NativeSignatureVerifier(),
            appChainService = appChainService
        )
    }

    private val installationRepoDefault: InstallationRepoDefault by depLazy {
        InstallationRepoDefault(
            objectRepo = objRepo,
            appChainService = appChainService,
            installationValidator = installationValidator,
            installationMetaRepo = installationMetaRepo
        )
    }

    private val greenfieldClient: GreenfieldClient by depLazy {
        GreenfieldClientHttp(
            netComponent.baseGfHost,
            greenfieldHttpClient,
        )
    }

    //
    // Public
    //
    override val installationMetaRepo: MutableInstallationMetaRepo by depLazy {
        ApkInstallationMetaRepo(
            context = app,
            storage = InstallationMetaRepoStorage(
                storageComponent.keyValueFactory.create(APP_META_STORAGE)
            )
        )
    }

    override val objectService: AssetService by depLazy {
        AssetServiceDefault(
            platformId,
            netComponent.baseClientHost,
            clientHttpClient,
            responseRepo
        )
    }

    override val objRepo: ObjectRepo by depLazy {
        ObjectRepoDefault(
            objectService = objectService,
            objectDao = storageComponent.appDatabase.getObjectDao()
        )
    }

    override val appUpdateInteractor: AppUpdateInteractor by depLazy {
        AppUpdateInteractorDefault(
            appChainService = appChainService,
            installationRepo = installationMetaRepo
        )
    }

    override val settingsRepo: SettingsRepo by depLazy {
        SettingsRepo(storageComponent.keyValueFactory.create(SETTINGS_STORAGE))
    }

    override val artifactRepo: ArtifactRepo by depLazy {
        ArtifactRepo()
    }

    override val artifactService: ArtifactService by depLazy {
        ArtifactServiceDefault(
            netComponent.baseClientHost,
            clientHttpClient,
        )
    }

    override val appChainService: AppChainService by depLazy {
        AppChainServiceEvm(storeAddress, oracleAddress,nodeJsonRpc, greenfieldClient)
    }

    override val profileRepo: ProfileRepo by depLazy {
        ProfileRepo()
    }

    override val profileService: ProfileRepo by depLazy {
        ProfileRepo()
    }

    override val installationRequestRepo: InstallationRequestRepo
        get() = installationRepoDefault

    override val installationQueue: InstallationRequestQueue
        get() = installationRepoDefault

    override val installationProvider: InstallationEventProducer
        get() = installationRepoDefault

    override val reportRepo: ReportRepo by depLazy {
        ReportRepoDefault()
    }

    override val reportService: ReportService by depLazy {
        ReportServiceDefault(
            netComponent.baseClientHost,
            clientHttpClient
        )
    }

    override val reviewService: ReviewService by depLazy {
        ReviewServiceDefault(
            netComponent.baseClientHost,
            clientHttpClient
        )
    }

    override val responseRepo: ResponseRepo by depLazy {
        ResponseRepoKeyValue(
            cache = storageComponent.keyValueFactory.create(REQUESTS_STORAGE),
            json = netComponent.json
        )
    }

    override val storeService: StoreService by depLazy {
        StoreServiceDefault(
            platformId,
            netComponent.baseClientHost,
            clientHttpClient,
            responseRepo
        )
    }

    override val storeInteractor: StoreInteractor by depLazy {
        StoreInteractorDefault(
            storeService = storeService,
            objDao = storageComponent.appDatabase.getObjectDao()
        )
    }

    override val statService: StatService by depLazy {
        StatServiceDefault(
            netComponent.baseStatHost,
            statHttpClient
        )
    }
}
