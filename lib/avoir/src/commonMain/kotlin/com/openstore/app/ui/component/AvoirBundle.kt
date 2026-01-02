package com.openstore.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AvoirBundleButton(
    modifier: Modifier = Modifier.padding(vertical = 16.dp),
    title: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    AvoirBundle(onClick = onClick) {
        Row(
            modifier = modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DefaultItemIcon(vector = icon, color = MaterialTheme.colorScheme.primary)
            DefaultSmallItemTitle(text = title, color = MaterialTheme.colorScheme.primary)
        }
    }
}

enum class AvoirBundleType {
    Default,
    Middle,
    Header,
    Footer,
}

fun defaultBundleType(size: Int, index: Int): AvoirBundleType {
    return  when  {
        size == 1 -> AvoirBundleType.Default
        index == 0 -> AvoirBundleType.Header
        index == size - 1 -> AvoirBundleType.Footer
        else -> AvoirBundleType.Middle
    }
}

internal object ObjectAvoirBundleDefaults {
    val CornerHeader: CornerBasedShape
        @Composable @ReadOnlyComposable get() {
        return MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0.0.dp),
            bottomStart = CornerSize(0.0.dp)
        )
    }

    val CornerFooter: CornerBasedShape
        @Composable @ReadOnlyComposable get() {
            return MaterialTheme.shapes.large.copy(
                topEnd = CornerSize(0.0.dp),
                topStart = CornerSize(0.0.dp)
            )
        }
}


@Composable
fun AvoirBundle(
    modifier: Modifier = Modifier
        .padding(horizontal = 19.dp, vertical = 8.dp),
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    bundleType: AvoirBundleType = AvoirBundleType.Default,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                when (bundleType) {
                    AvoirBundleType.Middle -> RectangleShape
                    AvoirBundleType.Default -> MaterialTheme.shapes.large
                    AvoirBundleType.Header -> ObjectAvoirBundleDefaults.CornerHeader
                    AvoirBundleType.Footer -> ObjectAvoirBundleDefaults.CornerFooter
                }
            )
            .background(backgroundColor)
            .run {
                if (onClick != null) {
                    clickable {
                        onClick()
                    }
                } else {
                    this
                }
            },
    ) {
        content()
    }
}
