package foundation.openstore.signer.app.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.openstore.app.ui.AppTheme
import com.openstore.app.ui.cells.DialogCheckCell
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.cells.TextCell
import com.openstore.app.ui.cells.TextValueCell
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.DefaultSmallItemIcon
import com.openstore.app.ui.icons.CommonIcons
import com.openstore.app.ui.icons.DarkMode
import com.openstore.app.ui.icons.Disco
import com.openstore.app.ui.icons.Help
import com.openstore.app.ui.icons.Telegram
import com.openstore.app.ui.icons.X
import com.openstore.app.ui.navigation.navigateToBrowser
import com.openstore.app.ui.setAppTheme
import com.openstore.app.ui.systemAppTheme
import com.openstore.app.mvi.props.observeSafeState
import foundation.openstore.signer.app.AppConfig
import foundation.openstore.signer.app.data.settings.SettingTheme
import foundation.openstore.signer.app.screens.SignerInjector
import foundation.openstore.kitten.android.withViewModel
import org.jetbrains.compose.resources.stringResource
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.Settings
import foundation.openstore.signer.app.generated.resources.ChooseTheme
import foundation.openstore.signer.app.generated.resources.General
import foundation.openstore.signer.app.generated.resources.Theme
import foundation.openstore.signer.app.generated.resources.System
import foundation.openstore.signer.app.generated.resources.Dark
import foundation.openstore.signer.app.generated.resources.Light
import foundation.openstore.signer.app.generated.resources.Contacts
import foundation.openstore.signer.app.generated.resources.Help
import foundation.openstore.signer.app.generated.resources.Telegram
import foundation.openstore.signer.app.generated.resources.XCom
import foundation.openstore.signer.app.generated.resources.Discord
import foundation.openstore.signer.app.generated.resources.Cancel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigator: NavHostController,
    onSecurity: () -> Unit,
) {
    val feature = SignerInjector.withViewModel { provideSettingsFeature() }
    val theme by feature.state.theme.observeSafeState()

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.Settings),
                onNavigateUp = {
                    navigator.navigateUp()
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 15.dp)
            ) {
                var isShowThemeChooser by remember { mutableStateOf(false) }
                if (isShowThemeChooser) {
                    ThemeDialog(
                        theme = theme,
                        onClose = { isShowThemeChooser = false },
                        onCheckedChange = { feature.sendAction(SettingsAction.SetTheme(it)) },
                    )
                }

                SmallTitleCell(stringResource(Res.string.General))

                TextValueCell(
                    image = { DefaultSmallItemIcon(CommonIcons.DarkMode) },
                    title = stringResource(Res.string.Theme),
                    value = when (theme) {
                        SettingTheme.System -> stringResource(Res.string.System)
                        SettingTheme.Dark -> stringResource(Res.string.Dark)
                        SettingTheme.Light -> stringResource(Res.string.Light)
                    },
                    onClick = { isShowThemeChooser = true }
                )

//                TextCell(
//                    image = { DefaultSmallItemIcon(Icons.Outlined.Security) },
//                    title = "Security",
//                    onClick = { onSecurity() }
//                )

                SmallTitleCell(stringResource(Res.string.Contacts))

                TextCell(
                    image = { DefaultItemIcon(CommonIcons.Help, size = 26.dp) },
                    title = stringResource(Res.string.Help),
                    onClick = { navigator.navigateToBrowser(AppConfig.Social.TgHelp) }
                )

                TextCell(
                    image = { DefaultItemIcon(CommonIcons.Telegram, size = 26.dp) },
                    title = stringResource(Res.string.Telegram),
                    onClick = { navigator.navigateToBrowser(AppConfig.Social.TgNews) }
                )

                TextCell(
                    image = { DefaultItemIcon(CommonIcons.X, size = 26.dp) },
                    title = stringResource(Res.string.XCom),
                    onClick = { navigator.navigateToBrowser(AppConfig.Social.X) }
                )

                TextCell(
                    image = {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outline)
                                .size(22.dp)
                        ) {
                            DefaultItemIcon(
                                modifier = Modifier.align(Alignment.Center),
                                vector = CommonIcons.Disco,
                                color = MaterialTheme.colorScheme.background,
                                size = 14.dp
                            )
                        }
                    },
                    title = stringResource(Res.string.Discord),
                    onClick = { navigator.navigateToBrowser(AppConfig.Social.Discord) }
                )
            }
        }
    }
}


@Composable
private fun ThemeDialog(
    theme: SettingTheme,
    onCheckedChange: (SettingTheme) -> Unit,
    onClose: () -> Unit
) {
    val systemTheme = systemAppTheme()

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                onClick = onClose,
                content = { Text(stringResource(Res.string.Cancel), color = MaterialTheme.colorScheme.onSurface) }
            )
        },
        title = {
            Text(stringResource(Res.string.ChooseTheme))
        },
        text = {
            Column {
                DialogCheckCell(stringResource(Res.string.System), theme == SettingTheme.System, onCheckedChange = {
                    onClose()
                    setAppTheme(systemTheme)
                    onCheckedChange(SettingTheme.System)
                })
                DialogCheckCell(stringResource(Res.string.Dark), theme == SettingTheme.Dark, onCheckedChange = {
                    onClose()
                    setAppTheme(AppTheme.Dark)
                    onCheckedChange(SettingTheme.Dark)
                })
                DialogCheckCell(stringResource(Res.string.Light), theme == SettingTheme.Light, onCheckedChange = {
                    onClose()
                    setAppTheme(AppTheme.Light)
                    onCheckedChange(SettingTheme.Light)
                })
            }
        }
    )
}
