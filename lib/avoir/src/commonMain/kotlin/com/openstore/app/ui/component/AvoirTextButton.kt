package com.openstore.app.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.component.DefaultItemIcon

@Composable
fun AvoirTextButton(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    icon: ImageVector? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val displayColor = if (enabled) color else color.copy(alpha = 0.38f)
    TextButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled
    ) {
        if (icon != null) {
            DefaultItemIcon(vector = icon, size = 20.dp, color = displayColor)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = title,
            color = displayColor,
            style = textStyle,
            fontWeight = FontWeight.Medium
        )
    }
}
