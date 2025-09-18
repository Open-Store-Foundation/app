package com.openstore.app.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.openstore.app.ui.cells.AvoirTextFieldCell
import com.openstore.app.ui.component.AvoirScaffold
import com.openstore.app.ui.component.AvoirToolbar
import com.openstore.app.ui.text.emptyTextValue

@Composable
fun CustomNodePreview() {
    ThemePreview {
        var bscNode = remember { emptyTextValue() }

        AvoirScaffold(
            topBar = {
                AvoirToolbar(
                    onNavigateUp = {  },
                    title = "Nodes"
                )
            }
        ) {
            Column(
                Modifier.padding(it)
            ) {
                AvoirTextFieldCell(
                    modifier = Modifier.fillMaxWidth(),
                    value = bscNode,
                    onValueChange = { bscNode = it },
                    label = "Node Url",
                    supportingText = { Text("Hello everybody") }
                )
            }
        }
    }
}