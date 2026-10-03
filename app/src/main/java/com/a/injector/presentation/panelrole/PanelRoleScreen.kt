package com.a.injector.presentation.panelrole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PanelRoleScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: PanelRoleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    PanelRoleScreen(
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
    PanelRoleScreen(
        navBackStack = rememberNavBackStack(),
        state = PanelRoleState(
            isRequestDetailsLoading = false,
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun PanelRoleScreen(
    navBackStack: NavBackStack<NavKey>,
    state: PanelRoleState,
    onAction: (PanelRoleAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.role_panel)
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
        visible = state.isActionBottomSheetVisible,
        onDismiss = { onAction(PanelRoleAction.DismissGrantRequestBottomSheet) },
        title = stringResource(R.string.action),
        content = {
            item {
                DefaultListItem(
                    content = { Text(text = state.requestToAction.profile.username) },
                    supportingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = stringResource(state.requestToAction.profile.role.title))
                            Icon(
                                modifier = Modifier
                                    .size(15.dp),
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = null
                            )
                            Text(text = stringResource(state.requestToAction.role.title))
                        }
                    }
                )
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(PanelRoleAction.DismissGrantRequestBottomSheet)
                    onAction(PanelRoleAction.DenyButton)
                },
                text = stringResource(R.string.decline),
                isError = true
            )
            CustomButton(
                onClick = {
                    onAction(PanelRoleAction.DismissGrantRequestBottomSheet)
                    onAction(PanelRoleAction.ConfirmButton)
                },
                text = stringResource(R.string.confirm)
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: PanelRoleState,
    onAction: (PanelRoleAction) -> Unit
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
        if (state.requestDetails.isEmpty()) {
            item("isRequestDetailsEmpty") {
                CustomCenterTextMessage(
                    modifier = Modifier
                        .animateItem(),
                    text = stringResource(R.string.empty)
                )
            }
            return@LazyColumn
        }
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
                    CustomIconButton(
                        onClick = {
                            onAction(PanelRoleAction.ShowGrantRequestBottomSheet(requestDetail))
                        },
                        content = { Icon(Icons.Rounded.Edit, null) }
                    )
                }
            )
        }
    }
}