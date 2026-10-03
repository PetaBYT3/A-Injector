package com.a.injector.presentation.role

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Icon
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
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.androidx.compose.koinViewModel

@Composable
fun RoleScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: RoleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    RoleScreen(
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
    RoleScreen(
        navBackStack = rememberNavBackStack(),
        state = RoleState(
            isProfileLoading = false,
            isRequestedRoleLoading = false
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun RoleScreen(
    navBackStack: NavBackStack<NavKey>,
    state: RoleState,
    onAction: (RoleAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.role)
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
                visible = !state.isContentLoading && !state.isRequested
            ) {
                CustomFloatingActionToolBar(
                    floatingActionButton = {
                        CustomFloatingActionButton(
                            onClick = { onAction(RoleAction.UpsertRoleButton) },
                            content = { Icon(Icons.Rounded.OpenInNew, null) },
                            isLoading = state.isUpsertRoleButtonLoading
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
    state: RoleState,
    onAction: (RoleAction) -> Unit,
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
            item("isContentError") {
                CustomCenterTextMessage(
                    modifier = Modifier
                        .animateItem(),
                    text = state.isProfileError.asString()
                )
            }
            return@LazyColumn
        }

        item("currentItem") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                content = { Text(text = stringResource(R.string.role_cur)) },
                supportingContent = { Text(text = stringResource(state.profile.role.title)) }
            )
        }
        spacer()
        val allowedRole = Role.allowedRoleToRequest.filterNot { role ->
            role == state.profile.role
        }
        item("requestedTitle") {
            DefaultListItem(
                index = 0,
                count = allowedRole.size + 1,
                content = { Text(text = stringResource(R.string.role_req)) },
                trailingContent = {
                    if (state.isRequested) {
                        Text(text = stringResource(R.string.waiting))
                    }
                }
            )
        }
        itemsIndexed(
            items = allowedRole
        ) { index, role ->
            DefaultClickableListItem(
                modifier = Modifier
                    .animateItem(),
                index = index + 1,
                count = allowedRole.size + 1,
                onClick = { if (!state.isRequested) onAction(RoleAction.RoleButton(role)) },
                leadingContent = {
                    RadioButton(
                        selected = state.requestedRole.role == role,
                        onClick = null
                    )
                },
                content = { Text(text = stringResource(role.title)) },
                supportingContent = { Text(text = stringResource(role.desc)) }
            )
        }
    }
}