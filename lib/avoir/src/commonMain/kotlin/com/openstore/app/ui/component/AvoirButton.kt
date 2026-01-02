package com.openstore.app.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openstore.app.ui.component.AvoirTinyLoader

@Composable
fun AvoirButton(
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    withPaddings: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
    shape: Shape = MaterialTheme.shapes.extraLarge,
    border: BorderStroke? = null,
    colors: ButtonColors = AvoirButtonDefaults.primaryButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    horizontalPadding: Dp = if (withPaddings) 19.dp else 0.dp,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .fillMaxWidth()
            .widthIn(min = 0.dp, max = 300.dp)
            .defaultMinSize(minHeight = 47.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = {
            val color = remember(enabled) { if (enabled) colors.contentColor else colors.disabledContentColor }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (loading) {
                Spacer(modifier = Modifier.width(8.dp))
                AvoirTinyLoader(color = MaterialTheme.colorScheme.outline)
            }
        },
    )
}

@Composable
fun OutlineAvoirButton(
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    withPaddings: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
    shape: Shape = MaterialTheme.shapes.extraLarge,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    colors: ButtonColors = AvoirButtonDefaults.outlineButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    horizontalPadding: Dp = if (withPaddings) 19.dp else 0.dp,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .fillMaxWidth()
            .widthIn(min = 0.dp, max = 300.dp)
            .defaultMinSize(minHeight = 46.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = {
            val color = remember(enabled) { if (enabled) colors.contentColor else colors.disabledContentColor }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (loading) {
                Spacer(modifier = Modifier.width(8.dp))
                AvoirTinyLoader(color = MaterialTheme.colorScheme.outline)
            }
        },
    )
}


@Composable
fun AvoirButtonSmall(
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
    shape: Shape = MaterialTheme.shapes.extraLarge,
    border: BorderStroke? = null,
    colors: ButtonColors = AvoirButtonDefaults.primaryButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit,
) {

    Button(
        onClick = onClick,
        modifier = modifier
            .height(42.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = {
            val color = remember(enabled) { if (enabled) colors.contentColor else colors.disabledContentColor }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 0.16.sp),
                color = color,
                fontWeight = FontWeight.SemiBold,
            )
            if (loading) {
                Spacer(modifier = Modifier.width(8.dp))
                AvoirTinyLoader(color = MaterialTheme.colorScheme.outline)
            }
        },
    )
}


@Composable
fun AvoirButtonTiny(
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
    shape: Shape = MaterialTheme.shapes.extraSmall,
    border: BorderStroke? = null,
    colors: ButtonColors = AvoirButtonDefaults.primaryButtonColors(),
    contentPadding: PaddingValues = AvoirButtonDefaults.TinyContentPadding,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(32.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = {
            val color = remember(enabled) { if (enabled) colors.contentColor else colors.disabledContentColor }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = color,
            )

            if (loading) {
                Spacer(modifier = Modifier.width(8.dp))
                AvoirTinyLoader(color = MaterialTheme.colorScheme.outline)
            }
        },
    )
}

@Composable
fun AvoirOutlineButtonSmall(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                MaterialTheme.shapes.small
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AvoirOutlineButtonSmall(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(vertical = 15.dp, horizontal = 19.dp)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                MaterialTheme.shapes.small
            )
            .clickable { onClick() }
            .padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        onContent()
    }
}

object AvoirButtonDefaults {

    private val ButtonHorizontalPadding = 10.dp
    private val ButtonVerticalPadding = 5.dp

    val TinyContentPadding = PaddingValues(
        start = ButtonHorizontalPadding,
        top = ButtonVerticalPadding,
        end = ButtonHorizontalPadding,
        bottom = ButtonVerticalPadding
    )

    @Composable
    fun primaryButtonColors(): ButtonColors {
        return ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    @Composable
    fun outlineButtonColors(): ButtonColors {
        return ButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.outline,
        )
    }

    @Composable
    fun secondaryButtonColors(): ButtonColors {
        return ButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.outline,
            disabledContentColor = MaterialTheme.colorScheme.outline,
        )
    }

    @Composable
    fun errorButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.outline,
            disabledContentColor = MaterialTheme.colorScheme.outline,
        )
    }
}
