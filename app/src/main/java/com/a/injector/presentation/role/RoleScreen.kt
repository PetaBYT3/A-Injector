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
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.RoleModel
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomCenterTextMessage
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomTextListTitle
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

        item("currentTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.current)
            )
        }
        item("currentItem") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                content = { Text(text = state.profile.role.title.asString()) },
                supportingContent = { Text(text = state.profile.role.desc.asString()) }
            )
        }
        spacer()
        item("requestedTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.requested)
            )
        }
        val allowedRole = Role.entries.filterNot { role ->
            role == Role.Administrator || role == state.profile.role
        }
        itemsIndexed(
            items = allowedRole
        ) { index, role ->
            DefaultClickableListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = allowedRole.size,
                onClick = { onAction(RoleAction.RoleButton(role)) },
                leadingContent = {
                    val isChecked = if (state.requestedRole != RoleModel.EMPTY) {
                        false
                    } else {
                        state.requestedRole.role == role
                    }
                    RadioButton(
                        selected = isChecked,
                        onClick = null
                    )
                },
                content = { Text(text = role.title.asString()) },
                supportingContent = { Text(text = role.desc.asString()) }
            )
        }
    }
}