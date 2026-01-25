package foundation.openstore.signer.app.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp
import com.openstore.app.ui.cells.TextValueCell
import com.openstore.app.ui.component.Label
import com.openstore.app.ui.component.TextOutlinePreviewImage
import foundation.openstore.signer.app.data.dao.SigningEntity
import foundation.openstore.signer.app.data.dao.SigningTarget
import foundation.openstore.signer.app.data.dao.Transaction
import foundation.openstore.signer.app.generated.resources.Connect
import foundation.openstore.signer.app.generated.resources.Res
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

val SigningEntity.shortDateDisplay: String
    @ReadOnlyComposable get() {
        val now = Clock.System.now()
        val diff = now - createdAt
        val minutes = diff.inWholeMinutes

        return when { // TODO format compose function
            minutes < 1 -> "1 min ago"
            minutes < 60 -> "$minutes min ago"
            minutes < 1440 -> "${diff.inWholeHours} hours ago"
            diff.inWholeDays < 7 -> "${diff.inWholeDays} days ago"
            else -> longDateDisplay
        }
    }

val SigningEntity.longDateDisplay: String
    @ReadOnlyComposable get() {
        val date = createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
        val month =  date.month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        return "${date.day} $month ${date.year} at ${date.hour}:${date.minute}"
    }

@Composable
fun TransactionCell(tx: Transaction, onClick: () -> Unit) {
    TextValueCell(
        title = tx.connection.serviceName,
        labels = { if (tx.signing.method == SigningTarget.Connection) Label(stringResource(Res.string.Connect)) },
        subtitle = tx.signing.shortDateDisplay,
        image = { TextOutlinePreviewImage("#${tx.signing.sequenceId}") },
        defaultMinSize = 68.dp,
        onClick = onClick
    )
}