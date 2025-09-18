package com.openstore.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openstore.app.data.CategoryId
import com.openstore.app.data.ObjTypeId
import com.openstore.app.data.ObjectId
import com.openstore.app.data.node.CustomNodeType
import com.openstore.app.data.report.ReportCategoryId
import com.openstore.app.data.report.ReportSubcategoryId
import com.openstore.app.screens.categories.CategoriesScreen
import com.openstore.app.screens.details.ObjDetailsScreen
import com.openstore.app.screens.feed.ChartFeedScreen
import com.openstore.app.screens.list.ObjListScreen
import com.openstore.app.screens.manage.ManageAppsScreen
import com.openstore.app.screens.node.AddCustomNodeScreen
import com.openstore.app.screens.node.CustomNodeScreen
import com.openstore.app.screens.report.ReportCategoryScreen
import com.openstore.app.screens.report.ReportSubcategoryScreen
import com.openstore.app.screens.report.ReportSubmitScreen
import com.openstore.app.screens.search.SearchScreen
import com.openstore.app.screens.settings.SettingsScreen
import com.openstore.app.ui.AvoirTheme
import kotlinx.serialization.Serializable

sealed interface Router {
    @Serializable
    data object Home : Router
    @Serializable
    data object Search : Router
    @Serializable
    data class Categories(
        val typeId: ObjTypeId,
    ) : Router

    @Serializable
    data class Objects(
        val typeId: ObjTypeId? = null,
        val categoryId: CategoryId? = null,
    ) : Router
    @Serializable
    data class ObjDetails(
        val id: Long? = null,
        val address: String? = null,
    ) : Router {
        companion object {
            fun withId(id: Long): ObjDetails = ObjDetails(id = id)
            fun withAddress(address: String): ObjDetails = ObjDetails(address = address)
        }

        fun toObjectId(): ObjectId {
            return when {
                id != null -> ObjectId.Id(id)
                address != null -> ObjectId.Address(address)
                else -> throw IllegalArgumentException("Invalid object ID or address")
            }
        }
    }

    @Serializable
    data object Settings : Router
    @Serializable
    data object ManageApps : Router
    @Serializable
    data object CustomNode : Router
    @Serializable
    data class AddCustomNode(
        val type: CustomNodeType
    ) : Router

    @Serializable
    data class ReportCategory(
        val objAddress: String,
    ) : Router
    @Serializable
    data class ReportSubcategory(
        val objAddress: String,
        val categoryId: ReportCategoryId,
    ) : Router
    @Serializable
    data class ReportSubmit(
        val objAddress: String,
        val categoryId: ReportCategoryId,
        val subcategoryId: ReportSubcategoryId? = null,
    ) : Router
}

@Composable
fun App(
    navigator: NavHostController
) {
    AvoirTheme {
        Surface {
            NavHost(navController = navigator, startDestination = Router.Home) {
                composable<Router.Home> {
                    ChartFeedScreen(
                        onSearch = {
                            navigator.navigate(Router.Search)
                        },
                        onSettings = {
                            navigator.navigate(Router.Settings)
                        },
                        onObject = { obj ->
                            navigator.navigate(Router.ObjDetails.withId(obj.id))
                        },
                    )
                }

//                composable<Router.Home> {
//                    FeedScreen(
//                        ObjTypeId.APP,
//                        onSeeMore = { objTypeId, expandType ->
//                            when (expandType) {
//                                is TitleType.BestInCategory -> navigator.navigate(Router.Objects(objTypeId, expandType.category))
//                                is TitleType.PopularCategories -> navigator.navigate(Router.Categories(objTypeId))
//                                is TitleType.TopChart -> navigator.navigate(Router.Objects(objTypeId))
//                                else -> {}
//                            }
//                        },
//                        onSearch = {
//                            navigator.navigate(Router.Search)
//                        },
//                        onSettings = {
//                            navigator.navigate(Router.Settings)
//                        },
//                        onObject = { obj ->
//                            navigator.navigate(Router.ObjDetails.withId(obj.id))
//                        },
//                        onCategory = { category ->
//                            navigator.navigate(Router.Objects(category.objectTypeId, category))
//                        }
//                    )
//                }

                composable<Router.ObjDetails> {
                    ObjDetailsScreen(
                        navigator = navigator,
                        onReport = { obj ->
                            navigator.navigate(Router.ReportCategory(obj.address))
                        }
                    )
                }

                composable<Router.Objects> {
                    ObjListScreen(
                        navigator = navigator,
                        onObject = { obj ->
                            navigator.navigate(Router.ObjDetails.withId(obj.id))
                        }
                    )
                }

                composable<Router.Search> {
                    SearchScreen(
                        navigator = navigator,
                        onObject = { obj ->
                            if (obj.isAttached) {
                                navigator.navigate(Router.ObjDetails.withId(obj.id))
                            } else {
                                navigator.navigate(Router.ObjDetails.withAddress(obj.address))
                            }
                        }
                    )
                }

                composable<Router.Settings> {
                    SettingsScreen(
                        navigator = navigator,
                        onManageApps = {
                            navigator.navigate(Router.ManageApps)
                        },
                        onCustomNode = {
                            navigator.navigate(Router.CustomNode)
                        },
                        onAppPage = {
                            navigator.navigate(Router.ObjDetails.withAddress(AppConfig.Env.StoreAppAddress))
                        }
                    )
                }

                composable<Router.ManageApps> {
                    ManageAppsScreen(
                        navigator = navigator,
                        onNavigateObject = { obj ->
                            navigator.navigate(Router.ObjDetails.withId(obj.id))
                        }
                    )
                }

                composable<Router.CustomNode> {
                    CustomNodeScreen(
                        navigator = navigator,
                        onAddCustom = { type ->
                            navigator.navigate(Router.AddCustomNode(type))
                        }
                    )
                }

                composable<Router.AddCustomNode> {
                    AddCustomNodeScreen(
                        navigator = navigator
                    )
                }

                composable<Router.Categories> {
                    CategoriesScreen(
                        navigator = navigator,
                        onCategory = { category ->
                            navigator.navigate(Router.Objects(category.objectTypeId, category))
                        }
                    )
                }

                composable<Router.ReportCategory> {
                    ReportCategoryScreen(
                        navigator = navigator,
                        onCategory = { address, id ->
                            navigator.navigate(Router.ReportSubcategory(address, id))
                        },
                        onSubmit = { address, id ->
                            navigator.navigate(Router.ReportSubmit(address, id, null))
                        }
                    )
                }

                composable<Router.ReportSubcategory> {
                    ReportSubcategoryScreen(
                        navigator = navigator,
                        onSubcategory = { address, categoryId, subcategory ->
                            navigator.navigate(Router.ReportSubmit(address, categoryId, subcategory))
                        },
                    )
                }

                composable<Router.ReportSubmit> {
                    ReportSubmitScreen(
                        navigator = navigator,
                        onSubmitted = {
                            navigator.popBackStack<Router.ObjDetails>(false)
                        },
                    )
                }
            }
        }
    }
}
