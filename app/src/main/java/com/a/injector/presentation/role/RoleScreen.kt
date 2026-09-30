package com.a.injector.presentation.role

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.OpenInNew
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
import com.a.injector.domain.model.RoleModel
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomCenterTextMessage
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.role.RoleStatus.Current
import com.a.injector.presentation.role.RoleStatus.Requested
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
        state = RoleState(),
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
    )

    CustomBottomSheet(
        visible = state.isRoleBottomSheetVisible,
        onDismiss = { onAction(RoleAction.UpsertRoleButton) },
        title = stringResource(R.string.role),
        content = {

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
        itemsIndexed(
            items = roleStatuses,
            key = { _, static -> static.id.name }
        ) { index, static ->
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = roleStatuses.size,
                content = { Text(text = stringResource(static.content)) },
                supportingContent = {
                    val supportingText = when (static.id) {
                        Current -> state.profile.role.title.asString()
                        Requested -> {
                            if (state.requestedRole == RoleModel.EMPTY) {
                                stringResource(R.string.no_role_requested)
                            } else {
                                state.requestedRole.role.title.asString()
                            }
                        }
                    }
                    Text(text = supportingText)
                },
                trailingContent = {
                    when (static.id) {
                        Current -> {}
                        Requested -> {
                            CustomIconButton(
                                onClick = { onAction(RoleAction.RoleBottomSheet) },
                                content = { Icon(Icons.Rounded.Edit, null) }
                            )
                        }
                    }
                }
            )
        }
    }
}