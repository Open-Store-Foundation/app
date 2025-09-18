package com.openstore.app.screens.cells

import androidx.compose.runtime.Composable
import com.openstore.app.data.TitleType
import com.openstore.app.ui.cells.TitleCell
import org.jetbrains.compose.resources.stringResource

@Composable
fun TitleTypeCell(
    expandType: TitleType,
    onSeeAll: (TitleType) -> Unit
) {
    TitleCell(
        text = when (expandType) {
            is TitleType.BestInCategory -> "Best in ${stringResource(expandType.category.displayRes())}"
            is TitleType.NewReleases -> "New Releases"
            is TitleType.PopularCategories -> "Categories"
            is TitleType.TopChart -> "Top Chart"
            is TitleType.BestInCategories -> "Best in categories"
        },
        onSeeAll = when (expandType) {
            is TitleType.NewReleases,
            is TitleType.BestInCategories -> null
            else -> {
                { onSeeAll(expandType) }
            }
        }
    )
}