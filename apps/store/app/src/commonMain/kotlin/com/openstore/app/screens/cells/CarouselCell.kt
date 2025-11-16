package com.openstore.app.screens.cells

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultRichItemImage
import com.openstore.app.data.Asset
import com.openstore.app.screens.catalog.FeedCell
import com.openstore.app.ui.cells.AvoirCardCell
import com.openstore.app.ui.component.AvoirVerifiedMark
import com.openstore.app.ui.component.DefaultItemImage
import com.openstore.app.ui.utils.dpToPx
import com.openstore.app.ui.utils.pxToDp

@Composable
fun CarouselCell(
    cell: FeedCell.Carousel,
    onClick: (Asset) -> Unit
) {
    val state = rememberPagerState { cell.items.size }

    val window = LocalWindowInfo.current
    val width = (window.containerSize.width - 120.dp.dpToPx()).pxToDp()

    HorizontalPager(
        modifier = Modifier.padding(top = 15.dp),
        state = state,
        pageSize = PageSize.Fixed(width),
        pageSpacing = 15.dp,
        contentPadding = PaddingValues(horizontal = 19.dp),
        key = { cell.items[it].target.id }
    ) {
        val obj = cell.items[it]
        val target = obj.target
        AvoirCardCell(
            image = {
                DefaultRichItemImage(
                    size = 40.dp,
                    image = target.logo.orEmpty(),
                    preview = "App"
                )
            },
            labels = {
                if (target.hasCheckmark) {
                    AvoirVerifiedMark()
                }
            },
            wrapPadding = true,
            onClick = { onClick(target) },
            title = target.name,
            subtitle = objectDescriptionAnnotation(target),
            isExpanded = true,
            expandableContent = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    DefaultItemImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(MaterialTheme.shapes.medium),
                        image = obj.cover,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        )
    }
}