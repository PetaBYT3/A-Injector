package com.a.injector.presentation.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTextListTitle
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.navigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SignInViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    
    val snackBarHostState = remember { SnackbarHostState() }
    ScreenEffectLauncher(
        snackBarHostState = snackBarHostState,
        screenEffect = viewModel.effect
    )

    Screen(
        navBackStack = navBackStack,
        state = state,
        onAction = onAction,
        snackBarHostState = snackBarHostState
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        navBackStack = rememberNavBackStack(),
        state = SignInState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: SignInState,
    onAction: (SignInAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.title_sign_in)
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
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: SignInState,
    onAction: (SignInAction) -> Unit
) {
    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        item("messageTitle") {
            CustomSurfaceText(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.message_sign_in)
            )
        }
        spacer()
        item("emailTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.item_email)
            )
        }
        item("signInEmail") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                placeholder = stringResource(R.string.item_email),
                value = state.emailTextField,
                onValueChange = { onAction(SignInAction.EmailTextField(it)) }
            )
        }
        spacer()
        item("passwordTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.item_password)
            )
        }
        item("signInPassword") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                placeholder = stringResource(R.string.item_password),
                value = state.passwordTextField,
                onValueChange = { onAction(SignInAction.PasswordTextField(it)) },
                trailingIcon = {
                    IconToggleButton(
                        checked = isPasswordVisible,
                        onCheckedChange = { isPasswordVisible = it },
                        content = {
                            Icon(
                                imageVector = if (isPasswordVisible) {
                                    Icons.Rounded.VisibilityOff
                                } else {
                                    Icons.Rounded.Visibility
                                },
                                contentDescription = null
                            )
                        }
                    )
                },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
        }
        spacer()
        item("signInAction") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .align(Alignment.TopStart),
                    onClick = { navBackStack.add(NavigationRoute.ResetPasswordScreen) },
                    content = { Text(text = "Forget Password") }
                )
                CustomButton(
                    modifier = Modifier
                        .height(ButtonDefaults.MediumContainerHeight)
                        .align(Alignment.TopEnd),
                    onClick = { onAction(SignInAction.SignInButton) },
                    text = stringResource(R.string.action_sign_in),
                    isLoading = state.isSingInButtonLoading
                )
            }
        }
    }
}