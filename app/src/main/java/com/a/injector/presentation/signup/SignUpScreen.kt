package com.a.injector.presentation.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.util.fastForEach
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
import com.a.injector.presentation.component.ScreenEffectLauncher
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.navigation.popBackStack
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SignUpViewModel = koinViewModel()
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
        state = SignUpState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: SignUpState,
    onAction: (SignUpAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.title_sign_up)
            )
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier
                    .padding(innerPadding),
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
    state: SignUpState,
    onAction: (SignUpAction) -> Unit
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
                text = stringResource(R.string.message_sign_up)
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
        item("signUpEmail") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                placeholder = stringResource(R.string.item_email),
                value = state.emailTextField,
                onValueChange = { onAction(SignUpAction.EmailTextField(it)) }
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
        item("signUpPassword") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                placeholder = stringResource(R.string.item_password),
                value = state.passwordTextField,
                onValueChange = { onAction(SignUpAction.PasswordTextField(it)) },
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
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                ) {
                    signUpPasswordRequirementItem.fastForEach { staticModel ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                tint = when (staticModel.id) {
                                    SignUpPasswordRequirementId.MoreThanEightCharacter -> {
                                        if (state.isPasswordMoreThan8Character) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    }
                                    SignUpPasswordRequirementId.ContainUppercase -> {
                                        if (state.isPasswordContainUppercase) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    }
                                    SignUpPasswordRequirementId.ContainNumber -> {
                                        if (state.isPasswordContainNumber) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    }
                                },
                                imageVector = when (staticModel.id) {
                                    SignUpPasswordRequirementId.MoreThanEightCharacter -> {
                                        if (state.isPasswordMoreThan8Character) {
                                            Icons.Rounded.Check
                                        } else {
                                            Icons.Rounded.Close
                                        }
                                    }
                                    SignUpPasswordRequirementId.ContainUppercase -> {
                                        if (state.isPasswordContainUppercase) {
                                            Icons.Rounded.Check
                                        } else {
                                            Icons.Rounded.Close
                                        }
                                    }
                                    SignUpPasswordRequirementId.ContainNumber -> {
                                        if (state.isPasswordContainNumber) {
                                            Icons.Rounded.Check
                                        } else {
                                            Icons.Rounded.Close
                                        }
                                    }
                                },
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(staticModel.contentTextResId),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                CustomButton(
                    modifier = Modifier
                        .height(ButtonDefaults.MediumContainerHeight)
                        .align(Alignment.TopEnd),
                    onClick = { onAction(SignUpAction.SignUpButton) },
                    text = stringResource(R.string.action_sign_in),
                    isLoading = state.isSignUpButtonLoading
                )
            }
        }
    }
}