package com.openstore.app.screens.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultRichItemImage
import com.openstore.app.screens.catalog.FeedCell
import com.openstore.app.ui.cells.AvoirCardCell
import com.openstore.app.ui.component.AvoirVerifiedMark

// TODO v3 make expandable when more apps
@Composable
fun HighlightCell(
    cell: FeedCell.Highlight,
    onClick: () -> Unit,
) {
//    var isExpanded by remember { mutableStateOf(true) }

    AvoirCardCell(
        modifier = Modifier.padding(top = 12.dp),
        image = {
            DefaultRichItemImage(
                size = 40.dp,
                image = cell.target.logo ?: "",
                preview = "App"
            )
        },
        labels = {
            if (cell.target.hasCheckmark) {
                AvoirVerifiedMark()
            }
        },
        onClick = onClick,
        title = cell.target.name,
        subtitle = objectDescriptionAnnotation(cell.target),
//        isExpanded = isExpanded,
//        trailingComponent = {
//            DefaultSmallItemIcon(
//                vector = if (isExpanded) {
//                    Icons.Default.KeyboardArrowUp
//                } else {
//                    Icons.Default.KeyboardArrowDown
//                }
//            )
//        },
//        expandableContent = {
//            Row(
//                Modifier.horizontalScroll(rememberScrollState()),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                for (cover in cell.covers) {
//                    DefaultItemImage(
//                        modifier = Modifier.size(180.dp, 120.dp),
//                        image = cover,
//                        contentScale = ContentScale.Crop,
//                    )
//                }
//            }
//        }
    )
}

@Composable
fun ScreenPreviewPlaceholder(
    width: Dp = 180.dp,
    height: Dp = 120.dp
) {
    Box(
        modifier = Modifier
            .size(width, height)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondary)
    )
}
