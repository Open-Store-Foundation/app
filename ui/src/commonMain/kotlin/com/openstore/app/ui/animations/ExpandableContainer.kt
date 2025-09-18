package com.openstore.app.ui.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val ANIMATION_DURATION = 300

@Composable
fun AnimatedContainer(
    isExpanded: Boolean,
    expandableContent: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
            expandVertically(animationSpec = tween(ANIMATION_DURATION)),
        exit = fadeOut(animationSpec = tween(ANIMATION_DURATION)) +
            shrinkVertically(animationSpec = tween(ANIMATION_DURATION))
    ) {
        Column {
            expandableContent()
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
