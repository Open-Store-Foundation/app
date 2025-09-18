package com.openstore.app.screens

import com.openstore.app.Router
import com.openstore.app.data.ObjectId
import com.openstore.app.data.node.CustomNodeType
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
import foundation.openstore.kitten.api.Component
import foundation.openstore.kitten.api.Injector

interface StoreComponent : Component {
    fun provideHomeFeature(): HomeFeature
    fun provideFeedFeature(): FeedFeature
    fun provideSearchFeature(): SearchFeature
    fun provideCategoriesFeature(data: Router.Categories): CategoriesFeature

    fun provideSettingsFeature(): SettingsFeature
    fun provideManageAppsFeature(): ManageAppsFeature
    fun provideCustomNodeFeature(): CustomNodeFeature
    fun provideAddCustomNodeFeature(type: CustomNodeType): AddCustomNodeFeature

    fun provideChartFeedFeature(): ChartFeedFeature
    fun provideObjListFeature(data: Router.Objects): ObjListFeature
    fun provideObjDetailsFeature(id: ObjectId): ObjDetailsFeature
    fun provideReviewFeature(): ObjReviewFeature

    fun provideReportCategoryFeature(data: Router.ReportCategory): ReportCategoryFeature
    fun provideReportSubcategoryFeature(data: Router.ReportSubcategory): ReportSubcategoryFeature
    fun provideReportSummaryFeature(data: Router.ReportSubmit): ReportSubmitFeature
}

object StoreInjector : Injector<StoreComponent>()
