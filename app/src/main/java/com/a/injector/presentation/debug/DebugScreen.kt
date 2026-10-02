package com.a.injector.presentation.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.mainnavigation.popBackStack
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DebugScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: DebugViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DebugScreen(
        navBackStack = navBackStack,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
@Preview
private fun Preview() {
    DebugScreen(
        navBackStack = rememberNavBackStack(),
        state = DebugState(),
        onAction = {}
    )
}

@Composable
private fun DebugScreen(
    navBackStack: NavBackStack<NavKey>,
    state: DebugState,
    onAction: (DebugAction) -> Unit
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = "Debug"
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onAction(DebugAction.Start) },
                    text = "Start",
                    isLoading = state.isStartButtonLoading
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1f),
                        value = state.textField,
                        onValueChange = { onAction(DebugAction.TextField(it)) }
                    )
                    CustomButton(
                        onClick = { onAction(DebugAction.Run) },
                        text = "Run"
                    )
                }
                Text(text = "Output:")
                Text(text = state.output)
            }
        }
    )
}