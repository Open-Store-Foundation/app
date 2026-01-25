package com.openstore.app.di

import android.app.Application
import com.openstore.app.AppConfig
import com.openstore.app.MainViewModel
import com.openstore.app.Router
import com.openstore.app.data.ObjectId
import com.openstore.app.data.PlatformId
import com.openstore.app.data.node.CustomNodeType
import com.openstore.app.data.store.ChartFeedInteractorDefault
import com.openstore.app.features.catalog.CatalogComponent
import com.openstore.app.features.catalog.CatalogInjector
import com.openstore.app.features.catalog.data.CatalogRepo
import com.openstore.app.features.catalog.data.CatalogRepoDefault
import com.openstore.app.features.catalog.screens.home.CatalogHomeFeature
import com.openstore.app.installer.handlers.ApkInstallationManager
import com.openstore.app.installer.InstallerComponent
import com.openstore.app.installer.InstallerInjector
import com.openstore.app.installer.ServiceController
import com.openstore.app.screens.StoreComponent
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.categories.CategoriesFeature
import com.openstore.app.screens.details.ObjDetailsFeature
import com.openstore.app.screens.catalog.ChartFeedFeature
import com.openstore.app.screens.catalog.FeedFeature
import com.openstore.app.screens.home.HomeFeature
import com.openstore.app.screens.list.ObjListFeature
import com.openstore.app.screens.manage.ManageAppsFeature
import com.openstore.app.screens.node.AddCustomNodeFeature
import com.openstore.app.screens.node.CustomNodeFeature
import com.openstore.app.screens.report.ReportCategoryFeature
import com.openstore.app.screens.report.ReportSubcategoryFeature
import com.openstore.app.screens.report.ReportSubmitFeature
import com.openstore.app.screens.review.ObjReviewFeature
import com.openstore.app.screens.search.SearchFeature
import com.openstore.app.screens.settings.SettingsFeature
import foundation.openstore.kitten.api.deps.depLazy
import foundation.openstore.kitten.core.ComponentRegistry
import foundation.openstore.kitten.core.Kitten

object OpenStoreInjection {
    fun init(app: Application) {
        Kitten.init(
            registry = OpenStoreComponentRegistry(app)
        ) { 
            create { netCmp }
            create { dataCmp }

            register(AppInjector) { appCmp }
            register(ActivityInjector) { mainCmp }
            register(CatalogInjector) { catalogCmp }
            register(StoreInjector) { storeCmp }
            register(InstallerInjector) { installerCmp }
        }
    }
}

class OpenStoreComponentRegistry(
    private val app: Application
) : ComponentRegistry() {

    val mdlCmp: StorageComponent by singleton {
        ModulesComponentDefault(app)
    }

    val netCmp: NetComponent by singleton {
        NetComponentDefault(app = app, appNodes = AppConfig.Nodes, modules = mdlCmp)
    }

    val dataCmp: DataComponent by singleton {
        DataComponentDefault(
            app = app,
            platformId = PlatformId.ANDROID,
            caip2Chain = AppConfig.Env.Caip2,
            storeAddress = AppConfig.Env.StoreAddress,
            oracleAddress = AppConfig.Env.OracleAddress,
            netComponent = netCmp,
            storageComponent = mdlCmp
        )
    }

    val appCmp: AppComponent by singleton {
        object : AppComponent {
            private val apkInstallationManager by depLazy {
                ApkInstallationManager(app, metaRepo = dataCmp.installationMetaRepo)
            }

            override val installerController: ServiceController by depLazy {
                ServiceController(
                    httpClient = netCmp.emptyHttpClient(),
                    queue = dataCmp.installationQueue,
                    eventProvider = dataCmp.installationProvider,
                    installationManager = apkInstallationManager,
                )
            }
        }
    }

    val installerCmp: InstallerComponent by shared<InstallerComponent> {
        object : InstallerComponent {
            override fun provideServiceController(): ServiceController {
                return appCmp.installerController
            }
        }
    }

    val mainCmp: MainComponent by shared<MainComponent> {
        object : MainComponent {
            override fun provideMainViewModel(): MainViewModel = MainViewModel(
                installationRepo = dataCmp.installationRequestRepo,
                settingsRepo = dataCmp.settingsRepo,
            )
        }
    }

    val catalogCmp: CatalogComponent by shared<CatalogComponent> {
        object : CatalogComponent {
            private val catalogRepo: CatalogRepo by depLazy { CatalogRepoDefault() }
            override fun provideHomeFeature(): CatalogHomeFeature = CatalogHomeFeature(catalogRepo)
        }
    }

    val storeCmp: StoreComponent by shared<StoreComponent> {
        object : StoreComponent {
            override fun provideHomeFeature(): HomeFeature = HomeFeature()

            override fun provideCategoriesFeature(
                data: Router.Categories,
            ): CategoriesFeature {
                return CategoriesFeature(data)
            }

            override fun provideObjDetailsFeature(
                id: ObjectId
            ): ObjDetailsFeature {
                return ObjDetailsFeature(
                    data = id,
                    objRepo = dataCmp.objRepo,
                    artifactService = dataCmp.artifactService,
                    appChainService = dataCmp.appChainService,
                    requestRepo = dataCmp.installationRequestRepo,
                    settingsRepo = dataCmp.settingsRepo,
                )
            }

            override fun provideSearchFeature(): SearchFeature {
                return SearchFeature(
                    objRepo = dataCmp.objRepo,
                    appChainService = dataCmp.appChainService
                )
            }

            override fun provideFeedFeature(): FeedFeature {
                return FeedFeature(
                    interactor = dataCmp.storeInteractor
                )
            }

            private val chartFeedInteractor by depLazy {
                ChartFeedInteractorDefault(dataCmp.objRepo, netCmp.networkProvider)
            }

            override fun provideChartFeedFeature(): ChartFeedFeature {
                return ChartFeedFeature(
                    chartFeed = chartFeedInteractor,
                    respRepo = dataCmp.responseRepo,
                )
            }

            override fun provideObjListFeature(
                data: Router.Objects,
            ): ObjListFeature {
                return ObjListFeature(
                    data = data,
                    objRepo = dataCmp.objRepo
                )
            }

            override fun provideReviewFeature(): ObjReviewFeature {
                return ObjReviewFeature()
            }

            override fun provideReportCategoryFeature(data: Router.ReportCategory): ReportCategoryFeature {
                return ReportCategoryFeature(data.objAddress, dataCmp.reportRepo)
            }

            override fun provideReportSubcategoryFeature(data: Router.ReportSubcategory): ReportSubcategoryFeature {
                return ReportSubcategoryFeature(
                    data.objAddress,
                    data.categoryId,
                    dataCmp.reportRepo
                )
            }

            override fun provideReportSummaryFeature(data: Router.ReportSubmit): ReportSubmitFeature {
                return ReportSubmitFeature(
                    data.objAddress,
                    data.categoryId,
                    data.subcategoryId,
                    dataCmp.reportService
                )
            }

            override fun provideSettingsFeature(): SettingsFeature {
                return SettingsFeature(
                    settingsRepo = dataCmp.settingsRepo
                )
            }

            override fun provideManageAppsFeature(): ManageAppsFeature {
                return ManageAppsFeature(
                    installationRepo = dataCmp.installationRequestRepo,
                    appUpdate = dataCmp.appUpdateInteractor
                )
            }

            override fun provideCustomNodeFeature(): CustomNodeFeature {
                return CustomNodeFeature(netCmp.nodeRepo)
            }

            override fun provideAddCustomNodeFeature(type: CustomNodeType): AddCustomNodeFeature {
                return AddCustomNodeFeature(type, netCmp.nodeRepo)
            }
        }
    }
}

