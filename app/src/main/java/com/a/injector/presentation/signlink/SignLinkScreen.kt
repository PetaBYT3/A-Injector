package com.a.injector.presentation.signlink

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomExtendedFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignLinkScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SignLinkViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    SignLinkScreen(
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
    SignLinkScreen(
        navBackStack = rememberNavBackStack(),
        state = SignLinkState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun SignLinkScreen(
    navBackStack: NavBackStack<NavKey>,
    state: SignLinkState,
    onAction: (SignLinkAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.sign_link)
            )
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier
                    .padding(innerPadding),
                navBackStack = navBackStack,
                state = state,
                onAction = onAction
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            CustomSlideUpAnimatedVisibility(
                visible = state.emailTextField.isNotBlank()
            ) {
                CustomFloatingActionToolBar(
                    floatingActionButton = {
                        CustomExtendedFloatingActionButton(
                            onClick = { onAction(SignLinkAction.SendLinkButton) },
                            content = { Text(text = stringResource(R.string.send)) },
                            isLoading = state.isSendLinkButtonLoading
                        )
                    }
                )
            }
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: SignLinkState,
    onAction: (SignLinkAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        item("messageTitle") {
            CustomSurfaceText(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.sign_link_desc)
            )
        }
        spacer()
        item("emailTextField") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                label = stringResource(R.string.email),
                value = state.emailTextField,
                onValueChange = { onAction(SignLinkAction.EmailTextField(it)) }
            )
        }
    }
}