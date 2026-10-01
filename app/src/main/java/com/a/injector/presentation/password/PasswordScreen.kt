package com.a.injector.presentation.password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
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
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.signup.PasswordRequirement
import com.a.injector.presentation.signup.passwordRequirements
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PasswordScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: PasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    PasswordScreen(
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
    PasswordScreen(
        navBackStack = rememberNavBackStack(),
        state = PasswordState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun PasswordScreen(
    navBackStack: NavBackStack<NavKey>,
    state: PasswordState,
    onAction: (PasswordAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.password)
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
                visible = state.passwordValid
            ) {
                CustomFloatingActionToolBar(
                    floatingActionButton = {
                        CustomFloatingActionButton(
                            onClick = { onAction(PasswordAction.ChangePasswordButton) },
                            content = { Icon(Icons.Rounded.Save, null) },
                            isLoading = state.isChangePasswordButtonLoading
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
    state: PasswordState,
    onAction: (PasswordAction) -> Unit,
) {
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        item("passwordTextField") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                label = stringResource(R.string.password),
                value = state.passwordTextField,
                onValueChange = { onAction(PasswordAction.PasswordTextField(it)) },
                trailingIcon = {
                    IconToggleButton(
                        checked = isPasswordVisible,
                        onCheckedChange = { isPasswordVisible = it },
                        content = {
                            val imageVector = when (isPasswordVisible) {
                                true -> Icons.Rounded.VisibilityOff
                                false -> Icons.Rounded.Visibility
                            }
                            Icon(imageVector, null)
                        }
                    )
                },
                visualTransformation = when (isPasswordVisible) {
                    true -> VisualTransformation.None
                    false -> PasswordVisualTransformation()
                }
            )
        }
        spacer()
        item("passwordValidation") {
            Column {
                passwordRequirements.fastForEach { staticModel ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isRequirementMet = when (staticModel.id) {
                            PasswordRequirement.HasEightCharacter -> state.passwordHasEightChar
                            PasswordRequirement.ContainUppercase -> state.passwordHasUppercase
                            PasswordRequirement.ContainNumber -> state.passwordHasNumber
                        }
                        val tint = when (isRequirementMet) {
                            true -> MaterialTheme.colorScheme.primary
                            false -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val imageVector = when (isRequirementMet) {
                            true -> Icons.Rounded.Check
                            false -> Icons.Rounded.Close
                        }
                        Icon(
                            modifier = Modifier
                                .size(20.dp),
                            tint = tint,
                            imageVector = imageVector,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(staticModel.content),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}