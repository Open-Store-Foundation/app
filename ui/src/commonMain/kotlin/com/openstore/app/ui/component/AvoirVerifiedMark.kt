package com.openstore.app.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.colors.achievementColor

@Composable
fun AvoirVerifiedMark(size: Dp = 16.dp) {
    Icon(
        Icons.Default.Verified,
        modifier = Modifier.size(size),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.achievementColor
    )
}