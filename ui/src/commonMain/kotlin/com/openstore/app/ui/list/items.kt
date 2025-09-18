package com.openstore.app.ui.list

import androidx.compose.foundation.lazy.LazyListScope
import com.openstore.app.ui.cells.AvoirErrorCell
import com.openstore.app.ui.cells.AvoirLoaderCell
import com.openstore.app.ui.component.AvoirEmptyCell

fun LazyListScope.errorItem(onRetry: () -> Unit) {
    item(key = "error", contentType = "error") {
        AvoirErrorCell(onRetry = onRetry)
    }
}

fun LazyListScope.loadingItem() {
    item(key = "loader", contentType = "loader") {
        AvoirLoaderCell()
    }
}

fun LazyListScope.notFoundItem() {
    item(key = "not_found", contentType = "not_found") {
        AvoirEmptyCell()
    }
}
