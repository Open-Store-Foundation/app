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
import com.openstore.app.log.L
import com.openstore.app.screens.StoreComponent
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.categories.CategoriesFeature
import com.openstore.app.screens.details.ObjDetailsFeature
import com.openstore.app.screens.feed.ChartFeedFeature
import com.openstore.app.screens.feed.FeedFeature
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
import org.openwallet.kitten.core.ComponentProvider
import org.openwallet.kitten.core.Kitten
import org.openwallet.kitten.core.depLazy

object OpenStoreInjection {
    fun init(app: Application) {
        Kitten.init(
            provider = OpenStoreComponentProvider(app)
        ) { deps ->
            create { deps.netCmp }
            create { deps.dataCmp }

            register(AppInjector) { deps.appCmp }
            register(ActivityInjector) { deps.mainCmp }
            register(CatalogInjector) { deps.catalogCmp }
            register(StoreInjector) { deps.storeCmp }
            register(InstallerInjector) { deps.installerCmp }
        }
    }
}

class OpenStoreComponentProvider(
    private val app: Application
) : ComponentProvider() {

    val mdlCmp: StorageComponent
        get() = singleOwner {
            ModulesComponentDefault(app)
        }

    val netCmp: NetComponent
        get() = singleOwner {
            NetComponentDefault(appNodes = AppConfig.Nodes, modules = mdlCmp)
        }

    val dataCmp: DataComponent
        get() = singleOwner {
            DataComponentDefault( PlatformId.ANDROID, AppConfig.Env.StoreAddress, netCmp, mdlCmp)
        }

    val appCmp: AppComponent by depLazy {
        object : AppComponent {
            private val apkInstallationManager by depLazy {
                ApkInstallationManager(app)
            }

            override val installerController: ServiceController by depLazy {
                L.d("Init LAZY NATIVE")
                ServiceController(
                    httpClient = netCmp.emptyHttpClient(),
                    queue = dataCmp.installationQueue,
                    eventProvider = dataCmp.installationProvider,
                    installationManager = apkInstallationManager,
                )
            }
        }
    }

    val installerCmp: InstallerComponent
        get() = multiOwner<InstallerComponent> {
            object : InstallerComponent {
                override fun provideServiceController(): ServiceController {
                    return appCmp.installerController
                }
            }
        }

    val mainCmp: MainComponent
        get() = multiOwner<MainComponent> {
            object : MainComponent {
                override fun provideMainViewModel(): MainViewModel = MainViewModel(
                    installationRepo = dataCmp.installationRequestRepo,
                    settingsRepo = dataCmp.settingsRepo,
                )
            }
        }

    val catalogCmp: CatalogComponent
        get() = multiOwner<CatalogComponent> {
            object : CatalogComponent {
                private val catalogRepo: CatalogRepo by depLazy { CatalogRepoDefault() }
                override fun provideHomeFeature(): CatalogHomeFeature = CatalogHomeFeature(catalogRepo)
            }
        }

    val storeCmp: StoreComponent
        get() = multiOwner<StoreComponent> {
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
                    return ReportSubcategoryFeature(data.objAddress, data.categoryId, dataCmp.reportRepo)
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

