package com.openstore.app.screens.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.openstore.app.AppConfig
import com.openstore.app.ui.AvoirTheme
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.appBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreUpdateDialog(
    onUpdate: (String) -> Unit,
    onCancel: () -> Unit
) {
    AvoirTheme {
        ModalBottomSheet(
            onDismissRequest = onCancel
        ) {
            AvoirToolbar(
                title = "Update available",
                colors = appBarColors(containerColor = BottomSheetDefaults.ContainerColor)
            )

            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    modifier = Modifier.size(45.dp),
                    imageVector = Icons.Default.Download,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text ="An improved version of Open Store is now available. Update for the latest features and performance enhancements.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )

                AvoirButton("Update Now") { onUpdate(AppConfig.Env.StoreAppAddress) }
            }
        }
    }
}