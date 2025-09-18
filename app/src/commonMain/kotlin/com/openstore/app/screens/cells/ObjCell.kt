package com.openstore.app.screens.cells

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openstore.app.data.Asset
import com.openstore.app.ui.cells.TextValueCell
import com.openstore.app.ui.component.AvoirVerifiedMark
import com.openstore.app.ui.component.DefaultRichItemImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun ObjCell(
    target: Asset,
    valueContent: ( @Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    val (title, content) = assetVerificationTitle(target)
    TextValueCell(
        image = {
            DefaultRichItemImage(
                size = 45.dp,
                image = target.logo ?: "",
                preview = "App"
            )
        },
        title = title,
        titleInline = content,
        subtitle = objectDescriptionAnnotation(target),
        onClick = onClick,
        defaultMinSize = 72.dp,
        content = valueContent
    )
}

private const val CONTEXT_ID = "verified-icon"

@Composable
fun assetVerificationTitle(asset: Asset): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val text = buildAnnotatedString {
        append(asset.name)

        if (asset.hasCheckmark) {
            append(" ")
            appendInlineContent(CONTEXT_ID, "[icon]")
        }
    }


    val inlineContent = mutableMapOf<String, InlineTextContent>()

    if (asset.hasCheckmark) {
        val content = InlineTextContent(
            Placeholder(
                width = 16.sp,
                height = 16.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            AvoirVerifiedMark()
        }

        inlineContent[CONTEXT_ID] = content
    }

    return text to inlineContent
}


@Composable
fun objectDescriptionAnnotation(target: Asset): AnnotatedString {
    return buildAnnotatedString {
        if (target.rating > 0) {
            withStyle(SpanStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.tertiary), { append("★") })
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold), { append(" ${target.formatedRating}") })
            append(" • ")
        }

        append(stringResource(target.category.displayRes()))
    }
}