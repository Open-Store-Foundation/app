package com.openstore.app.screens.cells

import androidx.compose.runtime.Composable
import com.openstore.app.ui.cells.TextIconCell
import com.openstore.app.data.CategoryId
import org.jetbrains.compose.resources.stringResource

@Composable
fun CategoryCell(target: CategoryId, onClick: () -> Unit) {
    TextIconCell(
        title = stringResource(target.displayRes()),
        image = target.icon(),
        onClick = { onClick() }
    )
}