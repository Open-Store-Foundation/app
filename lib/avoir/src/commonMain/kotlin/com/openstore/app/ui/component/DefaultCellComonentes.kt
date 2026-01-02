package com.openstore.app.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import com.openstore.app.ui.workround.isEmpty

///////
@Composable
fun DefaultSmallItemIcon(
    vector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = MaterialTheme.colorScheme.outline,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) = DefaultItemIcon(vector, modifier, size, color, contentDescription, onClick)

@Composable
fun DefaultSmallItemIcon(
    vector: ImageBitmap,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = MaterialTheme.colorScheme.outline,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) = DefaultItemIcon(vector, modifier, size, color, contentDescription, onClick)


@Composable
fun DefaultTinyItemIcon(
    vector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    color: Color = MaterialTheme.colorScheme.outline,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) = DefaultItemIcon(vector, modifier, size, color, contentDescription, onClick)

@Composable
fun DefaultItemIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.outline,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Icon(
        modifier = modifier.defaultIconModifier(size, onClick),
        painter = painter,
        contentDescription = contentDescription,
        tint = color
    )
}

@Composable
fun DefaultItemIcon(
    painter: ImageBitmap,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.outline,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Icon(
        modifier = modifier.defaultIconModifier(size, onClick),
        bitmap = painter,
        contentDescription = contentDescription,
        tint = color
    )
}

@Composable
fun DefaultItemIcon(
    vector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.outline,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Icon(
        modifier = modifier.defaultIconModifier(size, onClick),
        imageVector = vector,
        contentDescription = contentDescription,
        tint = color
    )
}

///////

@Composable
fun CircleIcon(
    vector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = vector,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )
    }
}

///////

@Composable
fun TextOutlinePreviewImage(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    textSize: TextUnit = 10.sp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    color: Color = MaterialTheme.colorScheme.outline,
    fontWeight: FontWeight? = null
) {
    Box(
        modifier = modifier
            .border(BorderStroke(1.dp, color), shape)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = fontWeight,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = textSize,
        )
    }
}

@Composable
fun TextEmojiPreviewImage(
    text: String,
    size: Dp = 46.dp,
    textSize: TextUnit = 24.sp,
    fontWeight: FontWeight? = null
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
                .copy(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                ),
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = textSize,
        )
    }
}

@Composable
fun TextPreviewImage(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    textSize: TextUnit = 10.sp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    fontWeight: FontWeight? = null
) {
    Box(
        modifier = modifier
            .background(containerColor, shape)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = fontWeight,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = textSize
        )
    }
}

@Composable
fun LoadingPreviewImage(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    color: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(color)
            .then(modifier)
    )
}

///////

@Composable
fun DefaultItemImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Image(
        modifier = modifier.defaultImageModifier(size, size, shape, onClick),
        painter = painter,
        contentDescription = contentDescription,
    )
}

@Composable
fun DefaultItemImage(
    image: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Image(
        modifier = modifier.defaultImageModifier(size, size, shape, onClick),
        imageVector = image,
        contentDescription = contentDescription,
    )
}

@Composable
fun DefaultItemImage(
    image: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    placeholder: Painter? = null,
    error: Painter? = placeholder,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
) {
    AvoirAsyncImage(
        link = image,
        placeholder = placeholder,
        error = error,
        modifier = modifier.defaultImageModifier(size, size, shape, onClick),
        contentDescription = contentDescription,
        onLoading = onState,
        onError = onState,
        onSuccess = onState,
        contentScale = contentScale,
    )
}

///////
@Composable
fun DefaultRichItemImage(
    image: String,
    modifier: Modifier = Modifier,
    preview: String = "",
    size: Dp = 36.dp,
    placeholder: Painter? = null,
    error: Painter? = placeholder,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
) {
    var isError by remember { mutableStateOf(image.isEmpty()) }
    var isPreview by remember { mutableStateOf(true) }

    Box {
        if (isError && error.isEmpty()) {
            TextOutlinePreviewImage(preview, modifier, size)
        }

        DefaultItemImage(
            image = image,
            size = size,
            placeholder = placeholder,
            modifier = modifier,
            onClick = onClick,
            error = error,
            contentDescription = contentDescription,
            onState = { state ->
                when (state) {
                    is AsyncImagePainter.State.Success -> {
                        isPreview = false
                        isError = false
                    }

                    is AsyncImagePainter.State.Error -> {
                        isPreview = false
                        isError = true
                    }

                    else -> Unit
                }
            },
            contentScale = contentScale,
        )

        if (isPreview && placeholder.isEmpty()) {
            LoadingPreviewImage(modifier, size)
        }
    }
}

private fun Modifier.defaultIconModifier(
    size: Dp,
    onClick: (() -> Unit)? = null,
): Modifier = composed {
    size(size)
        .run {
            if (onClick != null) {
                clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false),
                    onClick = onClick
                )
            } else {
                this
            }
        }
}


private fun Modifier.defaultImageModifier(
    width: Dp,
    height: Dp,
    shape: Shape,
    onClick: (() -> Unit)? = null,
): Modifier = composed {
    size(width, height)
        .clip(shape)
        .run {
            if (onClick != null) {
                clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false),
                    onClick = onClick
                )
            } else {
                this
            }
        }
}

///////

@Composable
fun DefaultLargeItemTitle(
    text: CharSequence,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = 1,
    inlineTextContent: Map<String, InlineTextContent> = emptyMap(),
) {
    InternalDefaultTitle(text, MaterialTheme.typography.titleLarge, modifier, color, maxLines, inlineTextContent)
}

@Composable
fun DefaultItemTitle(
    text: CharSequence,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
) {
    InternalDefaultTitle(text, MaterialTheme.typography.titleMedium, modifier, color, maxLines, inlineContent)
}

@Composable
fun DefaultSmallItemTitle(
    text: CharSequence,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = 1,
    inlineTextContent: Map<String, InlineTextContent> = emptyMap(),
) {
    InternalDefaultTitle(text, MaterialTheme.typography.titleSmall, modifier, color, maxLines, inlineTextContent)
}

@Composable
fun DefaultTinyItemTitle(
    text: CharSequence,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = 1,
    inlineTextContent: Map<String, InlineTextContent> = emptyMap(),
) {
    InternalDefaultTitle(text, MaterialTheme.typography.labelMedium, modifier, color, maxLines, inlineTextContent)
}

@Composable
private fun InternalDefaultTitle(
    text: CharSequence,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = 1,
    inlineTextContent: Map<String, InlineTextContent> = emptyMap(),
) {
    when (text) {
        is AnnotatedString -> Text(
            modifier = modifier,
            text = text,
            inlineContent = inlineTextContent,
            style = style,
            color = color,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines,
        )

        else -> Text(
            modifier = modifier,
            text = text.toString(),
            style = style,
            color = color,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines,
        )
    }
}

@Composable
fun DefaultItemSubtitle(
    text: CharSequence,
    textAlign: TextAlign? = null,
    maxLines: Int = 2,
    fontSize: TextUnit = 12.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    when (text) {
        is AnnotatedString -> Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            maxLines = maxLines,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )

        else -> Text(
            text = text.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = color,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            maxLines = maxLines,
            fontSize = fontSize,
            lineHeight = lineHeight,
        )
    }
}
