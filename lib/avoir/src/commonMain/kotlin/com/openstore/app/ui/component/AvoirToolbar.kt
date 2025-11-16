package com.openstore.app.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

val DefaultToolbarHeight = 52.dp

@Composable
fun AvoirToolbar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    actions: @Composable (RowScope.() -> Unit)? = null,
    onNavigateUp: (() -> Unit)? = null,
    titleIcon: (@Composable () -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = appBarColors(),
) {
    AvoirToolbar(
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
                        contentDescription = "Back",
                    )
                }
            }
        },
        actions = actions,
        titleIcon = titleIcon,
        scrollBehavior = scrollBehavior,
        colors = colors,
    )
}

// With custom icon/title
@Composable
fun AvoirToolbar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    titleIcon: (@Composable () -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = appBarColors(),
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = Modifier
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
                    contentAlignment = Alignment.Center
                ) {
                    it()
                }
            }
        },
        actions = {
            actions?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    it()
                }
            }
        },
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun appBarColors(
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    scrolledContainerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    navigationIconContentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    titleContentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        scrolledContainerColor = scrolledContainerColor,
        navigationIconContentColor = navigationIconContentColor,
        actionIconContentColor = actionIconContentColor,
        titleContentColor = titleContentColor,
    )
}
