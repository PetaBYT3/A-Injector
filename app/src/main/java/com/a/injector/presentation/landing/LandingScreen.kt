@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.BottomSheet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.util.CustomTonalButton
import com.a.injector.presentation.util.SnackBarEffectLauncher
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LandingScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: LandingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val snackBarHostState = remember { SnackbarHostState() }

    Screen(
        navBackStack = navBackStack,
        state = state,
        onAction = onAction,
        snackBarHostState = snackBarHostState
    )

    SnackBarEffectLauncher(
        snackBarHostState = snackBarHostState,
        screenEffect = viewModel.effect
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        navBackStack = rememberNavBackStack(),
        state = LandingState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: LandingState,
    onAction: (LandingAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden
    )

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .align(Alignment.Center),
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium
                )
                Button(
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .height(ButtonDefaults.MediumContainerHeight)
                        .align(Alignment.BottomCenter),
                    onClick = {
                        scope.launch {
                            bottomSheetState.expand()
                        }
                    },
                    content = { Text(text = stringResource(R.string.action_get_started)) }
                )
                BottomSheet(
                    state = bottomSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { navBackStack.add(NavigationRoute.SignInScreen) },
                            content = { Text(text = stringResource(R.string.title_sign_in)) }
                        )
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { navBackStack.add(NavigationRoute.SignUpScreen) },
                            content = { Text(text = stringResource(R.string.title_sign_up)) }
                        )
                        CustomTonalButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { onAction(LandingAction.ButtonSignGuest) },
                            text = stringResource(R.string.title_guest),
                            isLoading = state.isGuestButtonLoading
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    )
}