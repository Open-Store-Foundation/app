package foundation.openstore.signer.app.screens.connections.cells

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.openstore.app.ui.cells.TextValueCell
import com.openstore.app.ui.component.AvoirAlertDialog
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.TextOutlinePreviewImage
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import org.jetbrains.compose.resources.stringResource
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.Delete
import foundation.openstore.signer.app.generated.resources.DeleteConnection
import foundation.openstore.signer.app.generated.resources.DeleteConnectionDescription
import foundation.openstore.signer.app.generated.resources.Cancel

@Composable
fun ConnectionCell(
    connection: ConnectionEntity,
    onDelete: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    TextValueCell(
        title = connection.serviceName,
        subtitle = connection.serviceOrigin,
        image = { TextOutlinePreviewImage(connection.serviceName.take(1).uppercase()) },
        content = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    DefaultItemIcon(Icons.Default.MoreHoriz)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(Res.string.Delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    )

    if (showDeleteDialog) {
        AvoirAlertDialog(
            title = stringResource(Res.string.DeleteConnection),
            text = stringResource(Res.string.DeleteConnectionDescription),
            confirmText = stringResource(Res.string.Delete),
            cancelText = stringResource(Res.string.Cancel),
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onCancel = { showDeleteDialog = false },
            onDismiss = { showDeleteDialog = false },
            isDangerous = true
        )
    }
}

