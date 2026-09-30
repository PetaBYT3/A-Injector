package com.a.injector.presentation.manageuser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
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
import com.a.injector.data.dto.Role
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.MessageListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
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
                title = stringResource(R.string.user_panel)
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
            CustomFloatingActionToolBar(
                floatingActionButton = {
                    CustomFloatingActionButton(
                        onClick = { onAction(ManageUserAction.UpsertProfileButton) },
                        content = { Icon(Icons.Rounded.Save, null) },
                        isLoading = state.isUpsertProfileButtonLoading
                    )
                }
            )
        }
    )

    CustomBottomSheet(
        visible = state.isSelectRoleBottomSheetVisible,
        onDismiss = { onAction(ManageUserAction.SelectRoleBottomSheet) },
        title = stringResource(R.string.verify),
        content = {
            val allowedRole = Role.entries.filterNot { role ->
                role == Role.Administrator || role == state.profile.role
            }
            itemsIndexed(
                items = allowedRole,
                key = { _, role -> role.name }
            ) { index, role ->
                DefaultClickableListItem(
                    index = index,
                    count = allowedRole.size,
                    onClick = {
                        onAction(ManageUserAction.SelectRoleButton(role))
                        onAction(ManageUserAction.SelectRoleBottomSheet)
                    },
                    content = { Text(text = role.name) }
                )
            }
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
                MessageListItem(
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
                onValueChange = {},
                readOnly = true
            )
        }
        spacer()
        item("nominalTextField") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                label = stringResource(R.string.support),
                value = state.profile.nominal.toString(),
                onValueChange = {},
                trailingIcon = { Text(text = "IDR") },
                readOnly = true
            )
        }
        spacer()
        spacer()
        item {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                label = stringResource(R.string.role),
                value = state.profile.role.name,
                onValueChange = {},
                trailingIcon = {
                    CustomIconButton(
                        onClick = { onAction(ManageUserAction.SelectRoleBottomSheet) },
                        content = { Icon(Icons.Rounded.Edit, null) }
                    )
                },
                readOnly = true
            )
        }
    }
}