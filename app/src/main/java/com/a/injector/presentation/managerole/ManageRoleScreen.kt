package com.a.injector.presentation.managerole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomCenterTextMessage
import com.a.injector.presentation.component.CustomTonalButton
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageRoleScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: ManageRoleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    Screen(
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
    Screen(
        navBackStack = rememberNavBackStack(),
        state = ManageRoleState(
            isRequestDetailsLoading = false,
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: ManageRoleState,
    onAction: (ManageRoleAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.title_panel)
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

    CustomBottomSheet(
        visible = state.isGrantRequestBottomSheetVisible,
        onDismiss = { onAction(ManageRoleAction.DismissGrantRequestBottomSheet) },
        title = stringResource(R.string.action_grant_permission),
        content = {
            item {
                DefaultListItem(
                    content = { Text(text = state.requestToGrant.profile.username) },
                    supportingContent = { Text(text = state.requestToGrant.role.name) }
                )
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(ManageRoleAction.DismissGrantRequestBottomSheet)
                    onAction(ManageRoleAction.GrantRequestButton)
                },
                text = stringResource(R.string.action_confirm)
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: ManageRoleState,
    onAction: (ManageRoleAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        if (state.isRequestDetailsLoading) {
            item("isRequestDetailsLoading") {
                CustomCenterCircularWavyProgressIndicator(
                    modifier = Modifier
                        .animateItem()
                )
            }
            return@LazyColumn
        }
        when {
            state.requestDetails.isEmpty() -> {
                item("isRequestDetailsEmpty") {
                    CustomCenterTextMessage(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.item_empty)
                    )
                }
            }
            else -> {
                itemsIndexed(
                    items = state.requestDetails,
                    key = { _, requestDetail -> requestDetail.id }
                ) { index, requestDetail ->
                    DefaultListItem(
                        modifier = Modifier
                            .animateItem(),
                        index = index,
                        count = state.requestDetails.size,
                        content = { Text(text = requestDetail.profile.username) },
                        supportingContent = { Text(text = requestDetail.role.name) },
                        trailingContent = {
                            CustomTonalButton(
                                onClick = {
                                    onAction(ManageRoleAction.ShowGrantRequestBottomSheet(requestDetail))
                                },
                                text = stringResource(R.string.action_grant_permission)
                            )
                        }
                    )
                }
            }
        }
    }
}