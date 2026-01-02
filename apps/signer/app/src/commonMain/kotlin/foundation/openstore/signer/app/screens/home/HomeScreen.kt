package foundation.openstore.signer.app.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openstore.app.mvi.props.observeSafeState
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.OutlineAvoirButton
import foundation.openstore.kitten.android.withStatelessViewModel
import foundation.openstore.signer.app.Links
import foundation.openstore.signer.app.generated.resources.And
import foundation.openstore.signer.app.generated.resources.CreateNew
import foundation.openstore.signer.app.generated.resources.ImportExisting
import foundation.openstore.signer.app.generated.resources.OpenLedger
import foundation.openstore.signer.app.generated.resources.PrivacyPolicy
import foundation.openstore.signer.app.generated.resources.Res
import foundation.openstore.signer.app.generated.resources.TermsAndPrivacy
import foundation.openstore.signer.app.generated.resources.TermsOfService
import foundation.openstore.signer.app.generated.resources.WelcomeTo
import foundation.openstore.signer.app.generated.resources.firebox
import foundation.openstore.signer.app.screens.SignerInjector
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun HomeScreen(
    onCreateNew: () -> Unit,
    onImportExisting: () -> Unit,
) {
    val feature = SignerInjector.withStatelessViewModel { provideHomeFeature() }
    val isLoading by feature.state.isLoading.observeSafeState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        AvoirScaffold(topBar = {}) {
            Column(Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(142.dp),
                        imageVector = vectorResource(Res.drawable.firebox),
                        contentDescription = null,
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        stringResource(Res.string.WelcomeTo),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        stringResource(Res.string.OpenLedger),
                        style = MaterialTheme.typography.displayMedium.copy(letterSpacing = 0.2.sp),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AvoirButton(stringResource(Res.string.CreateNew)) {
                        onCreateNew()
                    }

                    OutlineAvoirButton(title = stringResource(Res.string.ImportExisting)) {
                        onImportExisting()
                    }

                    val color = MaterialTheme.colorScheme.primary
                    val linkStyle = remember(color) {
                        TextLinkStyles(
                            style = SpanStyle(
                                color = color,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }

                    val text = buildAnnotatedString {
                        append(stringResource(Res.string.TermsAndPrivacy))

                        withLink(
                            LinkAnnotation.Url(
                                url = Links.Terms,
                                styles = linkStyle
                            )
                        ) {
                            append(stringResource(Res.string.TermsOfService))
                        }

                        append(stringResource(Res.string.And))

                        withLink(
                            LinkAnnotation.Url(
                                url = Links.Privacy,
                                styles = linkStyle
                            )
                        ) {
                            append(stringResource(Res.string.PrivacyPolicy))
                        }
                    }

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        text = text,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
