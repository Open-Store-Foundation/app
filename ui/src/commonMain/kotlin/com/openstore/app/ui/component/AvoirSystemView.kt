package com.openstore.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.icons.CommonIcons
import com.openstore.app.ui.icons.Search

@Composable
fun AvoirEmptyCell() {
    AvoirSystemView(
        image = CommonIcons.Search,
        title = "Not found anything!",
    )
}

@Composable
fun AvoirEmptyScreen() {
    AvoirSystemScreen(
        image = CommonIcons.Search,
        title = "Not found anything!",
    )
}

@Composable
fun AvoirErrorScreen(
    onAction: () -> Unit,
) {
    AvoirSystemScreen(
        image = Icons.Default.ErrorOutline,
        title = "Something went wrong!",
        action = "Retry",
        onAction = onAction,
    )
}

@Composable
fun AvoirSystemScreen(
    image: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        AvoirSystemView(
            image = image,
            modifier = modifier,
            title = title,
            action = action,
            onAction = onAction,
            isFullScreen = true,
        )
    }
}

@Composable
fun AvoirSystemView(
    image: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    isFullScreen: Boolean = false,
    onAction: () -> Unit = {},
) {
    CustomAvoirSystemView(
        image = {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = image,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null,
            )
        },
        title = title,
        modifier = modifier,
        isFullScreen = isFullScreen,
        content = {
            if (action != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = action,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        },
    )
}

@Composable
fun CustomAvoirSystemView(
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit,
    title: String,
    content: @Composable () -> Unit = {},
    isFullScreen: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 19.dp, end = 19.dp, bottom = 26.dp, top = if (isFullScreen) 8.dp else 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        image()

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(15.dp))

        content.invoke()
    }
}
