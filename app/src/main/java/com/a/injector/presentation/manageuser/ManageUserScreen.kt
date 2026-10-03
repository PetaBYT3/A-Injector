package com.a.injector.presentation.manageuser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.domain.model.state.Role
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomCenterTextMessage
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomHorizontalToolBar
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.IdrVisualTransformation
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ManageUserScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    profileId: String,
    viewModel: ManageUserViewModel = koinViewModel(
        parameters = {
            parametersOf(profileId)
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    ManageUserScreen(
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
    ManageUserScreen(
        navBackStack = rememberNavBackStack(),
        state = ManageUserState(
            isProfileLoading = false
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun ManageUserScreen(
    navBackStack: NavBackStack<NavKey>,
    state: ManageUserState,
    onAction: (ManageUserAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.edit)
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
            CustomHorizontalToolBar(
                floatingActionButton = {
                    CustomFloatingActionButton(
                        onClick = { onAction(ManageUserAction.UpsertProfileButton) },
                        content = Icons.Rounded.Save,
                        isLoading = state.isUpsertProfileButtonLoading
                    )
                }
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: ManageUserState,
    onAction: (ManageUserAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        if (state.isContentLoading) {
            item("isContentLoading") {
                CustomCenterCircularWavyProgressIndicator(
                    modifier = Modifier
                        .animateItem()
                )
            }
            return@LazyColumn
        }
        if (state.isProfileError != null) {
            item("isProfileError") {
                CustomCenterTextMessage(
                    modifier = Modifier
                        .animateItem(),
                    text = state.isProfileError.asString()
                )
            }
            return@LazyColumn
        }
        item("userNameTextField") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                label = stringResource(R.string.username),
                value = state.profile.username,
                onValueChange = { onAction(ManageUserAction.UsernameTextField(it)) }
            )
        }
        spacer()
        item("nominalTextField") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                label = stringResource(R.string.support),
                value = state.profile.support.toString(),
                onValueChange = { userInput ->
                    val filteredInput = userInput.filter { it.isDigit() }
                    onAction(ManageUserAction.SupportTextField(filteredInput))
                },
                visualTransformation = IdrVisualTransformation(""),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
        spacer()
        itemsIndexed(
            items = Role.allRole,
            key = { _, role -> role.name }
        ) { index, role ->
            DefaultClickableListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = Role.allRole.size,
                onClick = { onAction(ManageUserAction.SelectRoleButton(role)) },
                leadingContent = {
                    RadioButton(
                        selected = state.profile.role == role,
                        onClick = null
                    )
                },
                content = { Text(text = stringResource(role.title)) },
                supportingContent = { Text(text = stringResource(role.desc)) }
            )
        }
    }
}