package com.openstore.app.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.openstore.app.common.strings.RString
import openstore.core.strings.generated.resources.Back
import org.jetbrains.compose.resources.stringResource

@Composable
fun AvoirAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    actions: @Composable (RowScope.() -> Unit)? = null,
    onNavigateUp: (() -> Unit)? = null,
    titleIcon: (@Composable () -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    AvoirAppBar(
        modifier = modifier,
        title = {
            if (title != null) {
                Text(
                    textAlign = TextAlign.Center,
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            if (onNavigateUp != null) {
                IconButton(onClick = onNavigateUp) {
                    DefaultItemIcon(
                        vector = navIcon,
                        contentDescription = stringResource(RString.Back),
                    )
                }
            }
        },
        actions = actions,
        titleIcon = titleIcon,
        scrollBehavior = scrollBehavior,
    )
}

// With custom icon/title
@Composable
fun AvoirAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    titleIcon: (@Composable () -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = Modifier
//                    .fillMaxHeight()
                    .wrapContentWidth(unbounded = true),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                titleIcon?.let {
                    it()
                    Spacer(modifier = Modifier.width(10.dp))
                }
                title()
            }
        },
        expandedHeight = DefaultToolbarHeight,
        navigationIcon = {
            navigationIcon?.let {
                Box(
                    Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    it()
                }
            }
        },
        actions = {
            actions?.let {
                Row(
                    Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    it()
                }
            }
        },
        colors = appBarColors(),
        scrollBehavior = scrollBehavior,
    )
}
