package com.a.injector.presentation.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.a.injector.R
import com.a.injector.presentation.util.CustomButton
import com.a.injector.presentation.util.CustomMediumTopAppBar
import com.a.injector.presentation.util.CustomTextListTitle
import com.a.injector.presentation.util.SnackBarEffectLauncher
import com.a.injector.presentation.util.spacer
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SignInViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    
    val snackBarHostState = remember { SnackbarHostState() }
    SnackBarEffectLauncher(
        snackBarHostState = snackBarHostState,
        screenEffect = viewModel.effect
    )

    Screen(
        state = state,
        onAction = onAction,
        snackBarHostState = snackBarHostState
    )
}

@Composable
private fun Screen(
    state: SignInState,
    onAction: (SignInAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )
    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            CustomMediumTopAppBar(
                scrollBehavior = scrollBehaviour,
                title = { Text(stringResource(R.string.title_sign_in)) }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    CustomTextListTitle(
                        text = stringResource(R.string.title_email)
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        placeholder = { Text(text = stringResource(R.string.title_email)) },
                        value = state.emailTextField,
                        onValueChange = { onAction(SignInAction.EmailTextField(it)) }
                    )
                }
                spacer()
                item {
                    CustomTextListTitle(
                        text = stringResource(R.string.title_password)
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        placeholder = { Text(text = stringResource(R.string.title_password)) },
                        value = state.passwordTextField,
                        onValueChange = { onAction(SignInAction.PasswordTextField(it)) },
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )
                }
                spacer()
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        ToggleButton(
                            checked = isPasswordVisible,
                            onCheckedChange = { isPasswordVisible = it },
                            content = { Text(text = stringResource(R.string.action_show_password)) }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            Surface(
                shape = CircleShape
            ) {
                CustomButton(
                    modifier = Modifier
                        .height(ButtonDefaults.MediumContainerHeight),
                    onClick = { onAction(SignInAction.SignInButton) },
                    text = stringResource(R.string.title_sign_in),
                    isLoading = state.isSingInButtonLoading,
                    enabled = state.isDataValid,
                )
            }
        }
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        state = SignInState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}