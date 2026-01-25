package foundation.openstore.signer.app.screens.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.openstore.app.ui.cells.AvoirTextFieldCell
import com.openstore.app.ui.component.AvoirButton
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.lifecycle.OnPause
import com.openstore.app.ui.lifecycle.OnResume
import com.openstore.app.ui.workround.rememberTextFieldValue
import org.jetbrains.compose.resources.stringResource
import foundation.openstore.signer.app.generated.resources.CreateNewWallet
import foundation.openstore.signer.app.generated.resources.WalletName
import foundation.openstore.signer.app.generated.resources.WalletNameDescription
import foundation.openstore.signer.app.generated.resources.Continue
import foundation.openstore.signer.app.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(
    navigator: NavHostController,
    onNext: (String) -> Unit
) {
    var name by rememberTextFieldValue()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OnResume {
        focusRequester.requestFocus()
    }

    OnPause {
        focusRequester.freeFocus()
        keyboardController?.hide()
    }

    AvoirScaffold(
        topBar = {
            AvoirToolbar(
                title = stringResource(Res.string.CreateNewWallet),
                onNavigateUp = { navigator.navigateUp() }
            )
        }
    ) {
        Column(
            Modifier.padding(it)
                .padding(vertical = 16.dp)
                .imePadding()
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                AvoirTextFieldCell(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = name,
                    onValueChange = {
                        if (it.text.length <= 15) {
                            name = it
                        }
                    },
                    placeholder = { Text(stringResource(Res.string.WalletName)) },
                    supportingText = { Text(stringResource(Res.string.WalletNameDescription)) },
                    keyboardActions = KeyboardActions(
                        onGo = {  },
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                    ),
                )
            }

            Column {
                AvoirButton(stringResource(Res.string.Continue), enabled = name.text.length >= 4) {
                    onNext(name.text)
                }
            }
        }
    }
}
