package com.openstore.app.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.openstore.app.AppConfig
import com.openstore.app.Router
import com.openstore.app.data.Artifact
import com.openstore.app.data.Asset
import com.openstore.app.installer.InstallationStatus
import com.openstore.app.log.L
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.screens.StoreInjector
import com.openstore.app.screens.cells.assetVerificationTitle
import com.openstore.app.ui.cells.AvoirErrorCell
import com.openstore.app.ui.cells.AvoirLoaderCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.cells.DescriptionCell
import com.openstore.app.ui.cells.RoundInfoCell
import com.openstore.app.ui.cells.SmallTitleCell
import com.openstore.app.ui.cells.TitleCell
import com.openstore.app.ui.colors.AvoirColorSetProviders
import com.openstore.app.ui.component.AvoirBundleOutlined
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirButtonDefaults
import com.openstore.app.ui.component.AvoirDivider
import com.openstore.app.ui.component.AvoirErrorScreen
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.DefaultRichItemImage
import com.openstore.app.ui.component.Label
import com.openstore.app.ui.component.OutlineAvoirButton
import com.openstore.app.ui.component.TextPreviewImage
import com.openstore.app.ui.lifecycle.OnCreate
import com.openstore.app.ui.navigation.navigateToBrowser
import com.openstore.app.ui.text.toAnnotatedLink
import com.openstore.app.ui.text.toAnnotatedString
import foundation.openstore.kitten.android.withViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjDetailsScreen(
    navigator: NavHostController,
    onReport: (Asset) -> Unit
) {
    val feature = StoreInjector.withViewModel {
        val obj = it.state.toRoute<Router.ObjDetails>()
        provideObjDetailsFeature(obj.toObjectId())
    }

    val obj by feature.state.obj.observeSafeState()
    val isLoading by feature.state.isLoading.observeSafeState()
    val isNotifyUpdate by feature.state.isNotifyUpdate.observeSafeState()
    val isError by feature.state.isError.observeSafeState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    OnCreate {
        feature.events.onEach { event ->
            when (event) {
                ObjDetailsEvents.SourcesNotFound -> {
                    snackbarHostState.showSnackbar("Installation file not found! Please try again later.")
                }
            }
        }.launchIn(scope)
    }

    var isOpened by remember { mutableStateOf(false) }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                onNavigateUp = {
                    navigator.navigateUp()
                },
                actions = {
                    obj?.let {
                        IconButton(
                            onClick = { isOpened = true }
                        ) {
                            DefaultItemIcon(vector = Icons.Default.MoreVert)
                        }
                    }

                    DropdownMenu(
                        expanded = isOpened,
                        onDismissRequest = { isOpened = false }
                    ) {
                        obj?.let {
                            DropdownMenuItem(
                                text = { Text("Report") },
                                leadingIcon = {
                                    DefaultItemIcon(vector = Icons.Default.Report)
                                },
                                onClick = {
                                    isOpened = false
                                    onReport(it)
                                }
                            )

                            // TODO v3 add notification feature
//                            isNotifyUpdate?.let { isNotifyUpdate ->
//                                DropdownMenuItem(
//                                    text = {
//                                        Text(
//                                            text = when (isNotifyUpdate) {
//                                                true -> "Mute updates"
//                                                false -> "Unmute updates"
//                                            }
//                                        )
//                                    },
//                                    leadingIcon = {
//                                        DefaultItemIcon(
//                                            vector = when (isNotifyUpdate) {
//                                                true -> Icons.Default.NotificationsOff
//                                                false -> Icons.Default.Notifications
//                                            }
//                                        )
//                                    },
//                                    onClick = {
//                                        isOpened = false
//                                        feature.sendAction(
//                                            ObjDetailsAction.SetNotification(
//                                                it.address,
//                                                !isNotifyUpdate
//                                            )
//                                        )
//                                    }
//                                )
//                            }

                            DropdownMenuItem(
                                text = { Text("Open explorer") },
                                leadingIcon = {
                                    DefaultItemIcon(vector = Icons.Default.DocumentScanner)
                                },
                                onClick = {
                                    isOpened = false
                                    navigator.navigateToBrowser(AppConfig.Env.explorer(it.address))
                                }
                            )

                            it.website?.let { website ->
                                DropdownMenuItem(
                                    text = { Text("Open website") },
                                    leadingIcon = {
                                        DefaultItemIcon(vector = Icons.Default.Explore)
                                    },
                                    onClick = {
                                        isOpened = false
                                        navigator.navigateToBrowser(website)
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Open assetlinks") },
                                    leadingIcon = {
                                        DefaultItemIcon(vector = Icons.Default.Link)
                                    },
                                    onClick = {
                                        isOpened = false
                                        navigator.navigateToBrowser(AppConfig.Env.assetlinks(website))
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) {
        Column(Modifier.padding(it)) {
            val safeObj = obj
            when {
                safeObj == null -> AvoirLoaderScreen()
                isError -> AvoirErrorScreen { feature.sendAction(ObjDetailsAction.Refresh) }
                else -> {
                    val state = rememberPullToRefreshState()

                    PullToRefreshBox(
                        state = state,
                        isRefreshing = isLoading,
                        onRefresh = { feature.sendAction(ObjDetailsAction.Refresh) },
                    ) {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 15.dp)
                        ) {
                            ObjectContent(feature, safeObj)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ObjectContent(
    feature: ObjDetailsFeature,
    obj: Asset,
) {
    val artifact by feature.state.artifact.observeSafeState()

    Column(
        modifier = Modifier.padding(horizontal = 19.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultRichItemImage(
            size = 72.dp,
            image = obj.logo ?: "",
            preview = "App"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val (title, inline) = assetVerificationTitle(
                asset = obj,
            )

            Text(
                text = title,
                inlineContent = inline,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            val website = obj.website
            if (obj.isOracleVerified == false || website.isNullOrEmpty()) {
                Label(
                    text = "No website",
                    badgeColor = MaterialTheme.colorScheme.error,
                    textColor = MaterialTheme.colorScheme.onError,
                )
            } else {
                Text(
                    text = website.toAnnotatedLink(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Column {
        val status by feature.state.status.observeSafeState()
        val progress by feature.state.progress.observeSafeState()

        val safeStatus = status
        val safeArtifact = artifact.data

        when {
            artifact.isLoading -> {
                AvoirLoaderCell(modifier = Modifier.padding(top = 18.dp))
            }
            artifact.isError -> {
                AvoirErrorCell(modifier = Modifier.padding(top = 16.dp)) {
                    feature.sendAction(ObjDetailsAction.Refresh)
                }
            }
            safeStatus != null && safeArtifact != null -> {
                ArtifactStatus(
                    obj = obj,
                    artifact = safeArtifact,
                    status = safeStatus,
                    progress = progress,
                    onInstall = { feature.sendAction(ObjDetailsAction.Install(obj, safeArtifact)) },
                    onCancel = { feature.sendAction(ObjDetailsAction.Cancel(obj)) },
                    onDelete = { feature.sendAction(ObjDetailsAction.Delete(obj)) },
                    onOpen = { feature.sendAction(ObjDetailsAction.Open(obj)) },
                )
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    AvoirDivider()

    Row(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 19.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AvoirAchievement(
            title = "Downloads",
            value = obj.formatedDownload,
            containerColor = AvoirColorSetProviders.Green.surfaceColor,
            contentColor = AvoirColorSetProviders.Green.primaryColor,
        )

        if (obj.rating > 0) {
            AvoirAchievement(
                title = "Rating",
                value = obj.formatedRating,
                containerColor = AvoirColorSetProviders.Purple.surfaceColor,
                contentColor = AvoirColorSetProviders.Purple.primaryColor,
            )
        }
    }

// TODO v3 screenshot feature
//
//    AvoirDivider()
//
//    TitleCell("Screenshots")
//
//    Row(
//        modifier = Modifier
//            .horizontalScroll(rememberScrollState())
//            .padding(vertical = 12.dp, horizontal = 19.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        ScreenshotPreviewPlaceholder()
//        ScreenshotPreviewPlaceholder()
//        ScreenshotPreviewPlaceholder()
//        ScreenshotPreviewPlaceholder()
//        ScreenshotPreviewPlaceholder()
//        ScreenshotPreviewPlaceholder()
//    }

    AvoirDivider()

// TODO v3 implement feature
//
//    artifact.data?.let { artifact ->
//        Column {
//            TitleCell("Last version")
//
//            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//
//                Column {
//                    SmallTitleCell("File checksum")
//                    DescriptionCell(artifact.checksum)
//                }
//
//                Column {
//                    SmallTitleCell("Version")
//                    DescriptionCell(
//                        buildString {
//                            if (artifact.versionName != null) {
//                                "${artifact.versionName} • "
//                            }
//
//                            append(artifact.versionCode)
//                        }
//                    )
//                }
//            }
//        }
//
//        AvoirDivider()
//    }

    Column {
        TitleCell("About asset")

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val clipboard = LocalClipboardManager.current

            if (obj.description != null) {
                Column {
                    SmallTitleCell("Description")
                    DescriptionCell(obj.description)
                }
            }

            Column(
                modifier = Modifier.clickable { clipboard.setText(obj.address.toAnnotatedString()) }
            ) {
                SmallTitleCell("Asset Address")
                DescriptionCell(obj.address)
            }

            Column(
                modifier = Modifier.clickable { clipboard.setText(obj.packageName.toAnnotatedString()) }
            ) {
                SmallTitleCell("Package name")
                DescriptionCell(obj.packageName)
            }

            Column {
                SmallTitleCell("Category")
                DescriptionCell(stringResource(obj.category.displayRes()))
            }
        }
    }
}

@Composable
private fun ArtifactStatus(
    obj: Asset,
    artifact: Artifact,
    status: InstallationStatus,
    progress: Int,
    onInstall: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit,
) {
    if (obj.hasCheckmark) {
        Spacer(Modifier.height(18.dp))
    } else {
        Spacer(Modifier.height(12.dp))

        RoundInfoCell(
            icon = Icons.Outlined.Info,
            title = "Please check domain before interacting!",
        )

        Spacer(Modifier.height(8.dp))
    }

    AnimatedVisibility(
        visible = status.isInstalling,
        enter = expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(durationMillis = 250)
        ),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(durationMillis = 250)
        )
    ) {
        Column(
            modifier = Modifier.padding(start = 19.dp, end = 19.dp, top = 8.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (status) {
                    InstallationStatus.Fetching -> "Fetching info..."
                    InstallationStatus.InQueue -> "In queue..."
                    InstallationStatus.Starting -> "Starting..."
                    InstallationStatus.Installing -> "Installing..."
                    else -> when (progress > 0) {
                        true -> "${progress}% (${artifact.formatedSize(progress)} / ${artifact.formattedSize})"
                        else -> "Downloading..."
                    }
                },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!status.isIntermediate && progress > 0) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { progress.toFloat() / 100 },
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    gapSize = 0.dp,
                )
            }
        }
    }

    Row(
        Modifier.padding(horizontal = 19.dp),
        horizontalArrangement = Arrangement.spacedBy(19.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(
            visible = status != InstallationStatus.Uninstalled,
            modifier = Modifier.weight(1f),
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(durationMillis = 250)
            ),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(durationMillis = 250)
            )
        ) {
            OutlineAvoirButton(
                withPaddings = false,
                enabled = status != InstallationStatus.Fetching,
                title = when (status) {
                    InstallationStatus.InstalledActual,
                    InstallationStatus.InstalledAnother,
                    InstallationStatus.InstalledOutdated -> "Delete"
                    else -> "Cancel"
                },
            ) {
                when (status) {
                    InstallationStatus.InstalledActual,
                    InstallationStatus.InstalledOutdated -> onDelete()
                    else -> onCancel()
                }
            }
        }

        AvoirButton(
            modifier = Modifier.weight(1f),
            withPaddings = false,
            enabled = status.isNone && !status.isDangerous,
            title = when (status) {
                InstallationStatus.Uninstalled -> "Install (${artifact.formattedSize})"
                InstallationStatus.InstalledAnother,
                InstallationStatus.InstalledActual -> "Open"
                InstallationStatus.InstalledOutdated -> "Update (${artifact.formattedSize})"
                else -> "Open"
            },
            colors = when (obj.isDangerous) {
                true -> AvoirButtonDefaults.errorButtonColors()
                else -> AvoirButtonDefaults.primaryButtonColors()
            },
        ) {
            when (status) {
                InstallationStatus.Uninstalled -> onInstall()
                else -> onOpen()
            }
        }
    }

    if (status.isDangerous) {
        Spacer(Modifier.height(8.dp))
        AvoirBundleOutlined(
            color = MaterialTheme.colorScheme.error
        ) {
            RoundInfoCell(
                icon = Icons.Outlined.Info,
                contentColor = MaterialTheme.colorScheme.error,
                title = "Application with same package name, but different address already installed!",
            )
        }
    }

    if (obj.isDangerous) {
        Spacer(Modifier.height(8.dp))

        AvoirBundleOutlined(
            color = MaterialTheme.colorScheme.error
        ) {
            RoundInfoCell(
                icon = Icons.Outlined.Info,
                contentColor = MaterialTheme.colorScheme.error,
                title = "Be careful, this application can be a malware!",
            )
        }
    }
}

@Composable
fun AvoirAchievement(
    title: String,
    value: String,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextPreviewImage(
            text = value,
            size = 52.dp,
            textSize = 12.sp,
            fontWeight = FontWeight.Bold,
            shape = CircleShape,
            containerColor = containerColor,
            contentColor = contentColor,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.width(80.dp),
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}
