package com.openstore.app.preview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openstore.app.common.strings.RString
import com.openstore.app.data.settings.SettingTheme
import com.openstore.app.screens.settings.SettingsAction
import com.openstore.app.ui.AppTheme
import com.openstore.app.ui.AvoirTheme
import com.openstore.app.ui.cells.AvoirCardCell
import com.openstore.app.ui.cells.AvoirDescriptionCell
import com.openstore.app.ui.cells.AvoirLoaderScreen
import com.openstore.app.ui.cells.AvoirStepCell
import com.openstore.app.ui.cells.AvoirTitledCardCell
import com.openstore.app.ui.cells.DescriptionCell
import com.openstore.app.ui.cells.DialogCheckCell
import com.openstore.app.ui.cells.FooterItemCell
import com.openstore.app.ui.cells.PropertyCell
import com.openstore.app.ui.cells.PropertyCellStyle
import com.openstore.app.ui.cells.RoundInfoCell
import com.openstore.app.ui.cells.TextCheckCell
import com.openstore.app.ui.cells.TextSwitchCell
import com.openstore.app.ui.cells.TextValueCell
import com.openstore.app.ui.cells.TitleCell
import com.openstore.app.ui.component.AvoirActionButton
import com.openstore.app.ui.component.AvoirBadge
import com.openstore.app.ui.component.AvoirBadgedBox
import com.openstore.app.ui.component.AvoirBundle
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirButtonSmall
import com.openstore.app.ui.component.AvoirButtonTiny
import com.openstore.app.ui.component.AvoirDivider
import com.openstore.app.ui.component.AvoirLoader
import com.openstore.app.ui.component.AvoirOutlineButtonSmall
import com.openstore.app.ui.component.AvoirSmallLoader
import com.openstore.app.ui.component.AvoirSurface
import com.openstore.app.ui.component.AvoirTextButton
import com.openstore.app.ui.component.AvoirTinyLoader
import com.openstore.app.ui.component.BadgeDirection
import com.openstore.app.ui.component.DefaultItemIcon
import com.openstore.app.ui.component.DefaultItemImage
import com.openstore.app.ui.component.DefaultItemTitle
import com.openstore.app.ui.component.Label
import com.openstore.app.ui.component.TextOutlinePreviewImage
import com.openstore.app.ui.preview.ThemePreview
import com.openstore.app.ui.setAppTheme
import openstore.core.strings.generated.resources.Delete
import org.jetbrains.compose.resources.stringResource

@Preview(device = Devices.NEXUS_5)
@Composable
fun PreviewBadges() {
    ThemePreview {
        Row(Modifier.padding(15.dp)) {
            AvoirBadgedBox(
                badge = "1",
                imageSize = 36.dp,
                image = { DefaultItemImage(image = Icons.Outlined.PersonOutline) },
                direction = BadgeDirection.EndTop,
            )

            Spacer(modifier = Modifier.width(15.dp))

            AvoirBadgedBox(
                badge = "1",
                imageSize = 36.dp,
                image = { DefaultItemImage(image = Icons.Outlined.PersonOutline) },
                direction = BadgeDirection.StartTop,
            )

            Spacer(modifier = Modifier.width(15.dp))

            AvoirBadgedBox(
                badge = "1",
                imageSize = 36.dp,
                image = { DefaultItemImage(image = Icons.Outlined.PersonOutline) },
                direction = BadgeDirection.EndBottom,
            )

            Spacer(modifier = Modifier.width(15.dp))

            AvoirBadgedBox(
                badge = "1",
                imageSize = 36.dp,
                image = { DefaultItemImage(image = Icons.Outlined.PersonOutline) },
                direction = BadgeDirection.StartBottom,
            )
        }
    }
}

@Preview(device = Devices.NEXUS_5)
@Composable
fun PreviewLoader() {
    ThemePreview {
        AvoirLoader()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(device = Devices.NEXUS_5)
@Composable
fun PreviewSlides() {
    ThemePreview {
        val state = rememberPagerState { 1 }
        Box {
            HorizontalPager(
                state = state,
                verticalAlignment = Alignment.Top,
            ) {
                AvoirBundle {
//                    HorizontalBannerSlide(
//                        modifier = Modifier.padding(horizontal = 15.dp),
//                        image = LogoIcons.Portal,
//                        title = "security screen layout",
//                        subtitle = "Test it",
//                        onHide = {},
//                        onClick = {},
//                    )
                }
            }

//            AvoirIndicator(
//                size = 1, state
//            )
        }
    }
}

@Preview(device = Devices.NEXUS_5)
@Composable
fun PreviewTextValueCell() {
    ThemePreview {
        TextValueCell( // see AssetCell
            image = {
                AvoirBadgedBox(
                    badge = "32",
                    imageSize = 40.dp,
                    image = {
                        TextOutlinePreviewImage(text = "App", modifier = Modifier.size(40.dp))
                    },
                )
            },
            labels = {
                Label("New")
            },
            title = "Vinance",
            subtitle = "100$",
            value = "3a",
            valueDescription = "dsa"
        )
    }
}

@Preview
@Composable
fun PreviewSwitchCell() {
    ThemePreview {
        Column {
            TextSwitchCell(
                image = {
                    TextOutlinePreviewImage(text = "ERC20", modifier = Modifier.size(40.dp))
                },
                title = "App",
                subtitle = "security screen layout, toggle button overlappingas the trailing edg",
                isChecked = true,
                onCheckedChange = {}
            )

            TextSwitchCell(
                image = {
                    TextOutlinePreviewImage(text = "ERC20", modifier = Modifier.size(40.dp))
                },
                title = "App",
                subtitle = "security screen layout, toggle button overlappingas the trailing edg",
                isChecked = false,
                onCheckedChange = {}
            )
        }
    }
}

@Preview
@Composable
fun PreviewTextCheckBoxCell() {
    ThemePreview {
        Column {
//            TextCheckBoxCell(
//                title = "I understand",
//                isChecked = true,
//                onCheckedChange = { }
//            )
//
//            TextCheckBoxCell(
//                title = "I understand",
//                isChecked = false,
//                onCheckedChange = { }
//            )
        }
    }
}

@Preview
@Composable
fun PreviewCheckBoxAgreementCell() {
    ThemePreview {
        Column {
//            CheckBoxAgreementCell(
//                title = "I understand that if I lose or forget my password\nI will lose access to my funds",
//                isChecked = true,
//                onCheckedChange = { }
//            )
//
//            CheckBoxAgreementCell(
//                title = "I understand that if I lose or forget my password\nI will lose access to my funds",
//                isChecked = false,
//                onCheckedChange = { }
//            )
        }
    }
}


@Preview
@Composable
fun PreviewTwLoader() {
    ThemePreview {
        Column {
            AvoirLoaderScreen()
            AvoirTinyLoader()
            AvoirSmallLoader()
        }
    }
}

@Preview
@Composable
fun FooterItemCellPreview() {
    ThemePreview {
        FooterItemCell(
            title = "Add new",
            vector = Icons.Rounded.AddCircle,
            onClick = { }
        )
    }
}

@Preview(heightDp = 800)
@Composable
fun PreviewSystemView() {
    ThemePreview {
//        AvoirSystemView(
//            image = Icons.Outlined.Info,
//            message = "Please put your backup file into folder and try again!",
//            action = "Open Google Drive",
//            secondaryAction = "One more",
//            onAction = {},
//        )
    }
}

@Preview
@Composable
fun PreviewCardImageCell() {
    ThemePreview {
//        CardImageCell(
//            imageUrl = "",
//            placeholder = rememberVectorPainter(image = LogoIcons.LockLogo),
//            title = "NftCollection",
//            modifier = Modifier.padding(16.dp),
//            imageSize = 40.dp,
//        )
    }
}

@Preview
@Composable
fun PreviewButton() {
    ThemePreview {
        Column {
            AvoirButton("Click") {}
            AvoirButtonSmall("Click") { }
            AvoirButtonTiny("Click") { }
            AvoirOutlineButtonSmall("Click") { }
            AvoirTextButton("Click") { }
        }
    }
}


@Preview
@Composable
fun PreviewInfoWarningCell() {
    ThemePreview {
        RoundInfoCell(
            icon = Icons.Outlined.Info,
            title = "Some information for user maybe it will be useful"
        )
    }
}

@Preview
@Composable
fun PreviewTextCheckCell() {
    ThemePreview {
        TextCheckCell(
            image = {
                TextOutlinePreviewImage(text = "ERC20", modifier = Modifier.size(40.dp))
            },
            title = "ETH",
            isChecked = true,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
fun PreviewActionCell() {
    ThemePreview {
        Row {
            AvoirActionButton(
                title = "Send",
                icon = Icons.Outlined.Info
            ) {}

            Spacer(modifier = Modifier.width(15.dp))

            AvoirActionButton(
                title = "Swap",
                icon = Icons.Outlined.Info,
                isEnabled = false
            ) {}
        }
    }
}

@Preview
@Composable
fun PreviewExpandableTitleCell() {
    ThemePreview {
        Column {
            TitleCell(
                text = "History",
                label = "Label"
            )
        }
    }
}

@Preview
@Composable
fun PreviewDescriptionCell() {
    ThemePreview {
        Column {
            AvoirDescriptionCell(text = "Some really long description info for user")
            AvoirDivider()
            DescriptionCell(description = stringResource(RString.Delete))
        }
    }
}

@Preview
@Composable
fun PreviewPropertyCell() {
    ThemePreview {
        Column {
            PropertyCell(title = "Description", value = stringResource(RString.Delete))
            PropertyCell(
                title = "Description",
                value = stringResource(RString.Delete),
                style = PropertyCellStyle.highlightTitleAndValue()
            )
        }
    }
}

//@Preview
//@Composable
//fun PreviewTwTag() {
//    ThemePreview {
//        AvoirTags(tags = listOf("BTC", "ETH", "ADA"))
//    }
//}

@Preview
@Composable
fun PreviewSystemDialog() {
    ThemePreview {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(
                    onClick = {  },
                    content = { Text("Cancel", color = MaterialTheme.colorScheme.onSurface) }
                )
            },
            title = {
                Text("Choose Theme")
            },
            text = {
                Column {
                    DialogCheckCell("System", true, onCheckedChange = {})
                    DialogCheckCell("Dark", false, onCheckedChange = {})
                    DialogCheckCell("Light", false, onCheckedChange = {})
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewHorizontalCardType() {
    ThemePreview {
        Column(Modifier.padding(15.dp)) {
            AvoirTitledCardCell(
                icon = {
                    TextOutlinePreviewImage(text = "App", modifier = Modifier.size(40.dp))
                },
                tag = "New",
                title = "Desposit",
                subtitle = "From exchange",
            )

            AvoirDivider()

            AvoirCardCell(
                image = {
                    TextOutlinePreviewImage(text = "App", modifier = Modifier.size(40.dp))
                },
                labels = {
                    Box(
                        Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.primary)
                            .clip(CircleShape)
                    )
                },
                tag = "Verified",
                title = "Desposit",
                subtitle = "From exchange",
            )
        }
    }
}

@Preview
@Composable
fun PreviewHorizontalCard() {
    ThemePreview {
        Box(Modifier.padding(15.dp)) {
            AvoirCardCell(
                onClick = { /*TODO*/ },
                icon = { modifier ->
                    TextOutlinePreviewImage(text = "App", modifier = modifier)
                },
                title = {
                    PropertyCell(
                        title = "MoonPay",
                        value = "≈ 0.0098 BTC",
                        style = PropertyCellStyle.highlightTitleAndValue(),
                        modifier = Modifier.padding(
                            start = 0.dp,
                            end = 15.dp,
                        ),
                    )
                },
                subtitle = {
                    PropertyCell(
                        title = "Fees Included",
                        value = " €2170,23.12",
                        modifier = Modifier.padding(top = 4.dp, end = 15.dp),
                    )
                },
            )
        }
    }
}

@Preview
@Composable
fun TwBadgePreview() {
    ThemePreview {
        Column {
            Row {
                AvoirBadge(text = "1")
                AvoirBadge(text = "12")
                AvoirBadge(text = "123")
                AvoirBadge(text = "1234")
            }

            Row {
                AvoirBadgedBox(
                    badge = "1",
                    imageSize = 36.dp,
                    image = { DefaultItemImage(image = Icons.Outlined.PersonOutline) },
                )

                AvoirBadgedBox(
                    badge = "123",
                    imageSize = 36.dp,
                    image = { DefaultItemImage(image = Icons.Outlined.PersonOutline) },
                )
            }
        }
    }
}

@Preview
@Composable
fun TwStepCellPreview() {
    ThemePreview {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvoirStepCell(isActive = false)
            AvoirStepCell(isActive = true)
        }
    }
}

