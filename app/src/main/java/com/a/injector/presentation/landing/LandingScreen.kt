@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LandingScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: LandingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LandingScreen(
        navBackStack = navBackStack,
        state = state,
        onAction = viewModel::onAction,
        snackBarHostState = snackBarHostState
    )

    ScreenEffectLauncher(
        snackBarHostState = snackBarHostState,
        screenEffect = viewModel.effect
    )
}

@Composable
@Preview
private fun Preview() {
    LandingScreen(
        navBackStack = rememberNavBackStack(),
        state = LandingState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun LandingScreen(
    navBackStack: NavBackStack<NavKey>,
    state: LandingState,
    onAction: (LandingAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        content = { innerPadding ->
            Content(
                modifier = Modifier
                    .padding(innerPadding),
                navBackStack = navBackStack,
                state = state,
                onAction = onAction
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: LandingState,
    onAction: (LandingAction) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(150.dp),
                painter = painterResource(R.drawable.inject),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.5.dp)
        ) {
            signOptions.fastForEachIndexed { index, staticModel ->
                DefaultClickableListItem(
                    index = index,
                    count = signOptions.size,
                    onClick = {
                        when (staticModel.id) {
                            SignOption.SignIn -> {
                                navBackStack.add(MainNavigationRoute.SignInScreen)
                            }
                            SignOption.SignUp -> {
                                navBackStack.add(MainNavigationRoute.SignUpScreen)
                            }
                            SignOption.Guest -> {
                                onAction(LandingAction.ButtonSignGuest)
                            }
                        }
                    },
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (staticModel.id) {
                                SignOption.SignIn, SignOption.SignUp -> {
                                    Text(text = stringResource(staticModel.contentTextResId))
                                }
                                SignOption.Guest -> {
                                    if (!state.isGuestButtonLoading) {
                                        Text(text = stringResource(staticModel.contentTextResId))
                                    } else {
                                        CircularWavyProgressIndicator(
                                            modifier = Modifier
                                                .size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}