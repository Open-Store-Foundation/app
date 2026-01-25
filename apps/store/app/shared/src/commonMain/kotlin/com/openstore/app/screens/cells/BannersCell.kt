package com.openstore.app.screens.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.openstore.app.data.Asset
import com.openstore.app.screens.catalog.FeedCell
import com.openstore.app.ui.cells.AvoirBannerCell
import com.openstore.app.ui.cells.AvoirCardTextCell
import com.openstore.app.ui.component.AvoirVerifiedMark
import com.openstore.app.ui.component.BigSquareLabel
import com.openstore.app.ui.component.DefaultItemImage
import com.openstore.app.ui.component.DefaultRichItemImage
import com.openstore.app.ui.utils.dpToPx
import com.openstore.app.ui.utils.pxToDp

@Composable
fun BannersCell(
    cell: FeedCell.Banner,
    onObject: (Asset) -> Unit,
) {
    val state = rememberPagerState { cell.items.size }

    Column {
        val window = LocalWindowInfo.current
        val width = (window.containerSize.width - 36.dp.dpToPx())
            .coerceAtMost(360.dp.dpToPx())
            .pxToDp()

        HorizontalPager(
            state = state,
            pageSize = if (cell.items.size == 1) PageSize.Fill else PageSize.Fixed(width),
            pageSpacing = 5.dp,
            contentPadding = PaddingValues(horizontal = 14.dp),
            key = { cell.items[it].target.id }
        ) {
            val obj = cell.items[it]
            val target = obj.target

            AvoirBannerCell(
                image = {
                    Box {
                        DefaultItemImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(MaterialTheme.shapes.medium),
                            image = obj.cover,
                            contentScale = ContentScale.Crop,
                        )

                        BigSquareLabel(
                            "#1 Books",
                            Modifier.align(Alignment.BottomStart),
                        )
                    }
                },
                cardTextCell = {
                    AvoirCardTextCell(
                        image = {
                            DefaultRichItemImage(
                                size = 40.dp,
                                image = target.logo.orEmpty(),
                                preview = "App"
                            )
                        },
                        defaultMinSize = 30.dp,
                        title = target.name,
                        subtitle = objectDescriptionAnnotation(target),
                        labels = {
                            if (target.hasCheckmark) {
                                AvoirVerifiedMark()
                            }
                        },
                    )
                },
                onClick = {
                    onObject(target)
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (cell.items.size > 1) {
            AvoirIndicator(
                size = 2,
                state = state,
            )
        }
    }
}

@Composable
fun AvoirIndicator(
    modifier: Modifier = Modifier,
    size: Int,
    state: PagerState
) {
    Row(
        modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        if (size > 1) {
            repeat(size) { iteration ->
                val color = when (state.currentPage == iteration) {
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.outline
                }

                val width = when (state.currentPage == iteration) {
                    true -> 12.dp
                    false -> 4.dp
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(
                            height = 4.dp,
                            width = width
                        ),
                )
            }
        }
    }
}
