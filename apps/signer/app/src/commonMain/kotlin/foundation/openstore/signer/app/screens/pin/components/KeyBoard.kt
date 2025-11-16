package foundation.openstore.signer.app.screens.pin.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.workround.gesturesDisabled

private const val PIN_KEYBOARD_ITEMS = 12

@Immutable
sealed class PinKeyboardItem {
    class Number(val num: Int) : PinKeyboardItem()
    data object BackSpace : PinKeyboardItem()
    data object FingerPrint : PinKeyboardItem()
}

@Composable
fun PinKeyBoard(
    isEnabled: () -> Boolean,
    isFingerEnabled: Boolean,
    isFingerVisible: Boolean,
    isBackspaceEnabled: () -> Boolean,
    modifier: Modifier = Modifier,
    onClick: (item: PinKeyboardItem) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier
            .gesturesDisabled(disabled = !isEnabled.invoke()),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 19.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center,
        userScrollEnabled = false
    ) {
        items(PIN_KEYBOARD_ITEMS, key = { it }) { item ->
            when (item) {
                11 -> PinKeyboardItem(
                    item = PinKeyboardItem.BackSpace,
                    color = if (!isEnabled.invoke() || !isBackspaceEnabled()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                    onClick = onClick,
                    isEnabled = isBackspaceEnabled()
                )
                10 -> PinKeyboardItem(
                    item = PinKeyboardItem.Number( 0),
                    color = if (!isEnabled.invoke()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    onClick = onClick
                )
                9 -> PinKeyboardItem(
                    item = PinKeyboardItem.FingerPrint,
                    color = if (!isEnabled.invoke() || !isFingerEnabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    onClick = onClick,
                    isVisible = isFingerVisible,
                    isEnabled = isFingerEnabled
                )
                else -> PinKeyboardItem(
                    item = PinKeyboardItem.Number( item + 1),
                    color = if (!isEnabled.invoke()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
internal fun PinKeyboardItem(
    item: PinKeyboardItem,
    color: Color,
    isVisible: Boolean = false,
    isEnabled: Boolean = false,
    onClick: (item: PinKeyboardItem) -> Unit,
) {
    when (item) {
        is PinKeyboardItem.Number -> {
            Text(
                text = "${item.num}",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onClick(item) },
                    indication = ripple(bounded = false, radius = 32.dp),
                ).padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
            )
        }
        is PinKeyboardItem.FingerPrint -> {
            PinClickableImage(
                Modifier
                    .gesturesDisabled(!isEnabled || !isVisible)
                    .padding(vertical = 12.dp)
                    .size(42.dp),
                Icons.Default.Fingerprint,
                color
            ) { onClick(item) }
        }
        is PinKeyboardItem.BackSpace -> {
            PinClickableImage(
                Modifier
                    .gesturesDisabled(!isEnabled)
                    .padding(vertical = 20.dp)
                    .size(30.dp),
                Icons.Default.Backspace,
                color
            ) { onClick(item) }
        }
    }
}

@Composable
private fun PinClickableImage(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
) {
    Image(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            onClick = { onClick() },
            indication = ripple(bounded = false, radius = 32.dp),
        ),
        colorFilter = ColorFilter.tint(color),
    )
}


