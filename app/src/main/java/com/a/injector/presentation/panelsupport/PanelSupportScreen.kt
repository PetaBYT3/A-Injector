package com.a.injector.presentation.panelsupport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomAsyncImage
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomCenterTextMessage
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import com.a.injector.presentation.util.toIdr
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PanelSupportScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: PanelSupportViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    PanelSupportScreen(
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
    PanelSupportScreen(
        navBackStack = rememberNavBackStack(),
        state = PanelSupportState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun PanelSupportScreen(
    navBackStack: NavBackStack<NavKey>,
    state: PanelSupportState,
    onAction: (PanelSupportAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.support_panel)
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
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    )

    CustomBottomSheet(
        visible = state.isSupportingBottomSheetVisible,
        onDismiss = { onAction(PanelSupportAction.DismissSupportBottomSheet) },
        title = stringResource(R.string.action),
        content = {
            item("supportingItem") {
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    content = {
                        Text(
                            text = state.supportingToAction.profile.username,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = { Text(text = state.supportingToAction.support.toIdr()) }
                )
            }
            spacer()
            item("proofImage") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    contentAlignment = Alignment.Center
                ) {
                    CustomAsyncImage(
                        imageSource = state.supportingToAction.imageUrl
                    )
                }
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(PanelSupportAction.DismissSupportBottomSheet)
                    onAction(PanelSupportAction.DenySupportButton)
                },
                text = stringResource(R.string.decline),
                isError = true
            )
            CustomButton(
                onClick = {
                    onAction(PanelSupportAction.DismissSupportBottomSheet)
                    onAction(PanelSupportAction.ConfirmSupportButton)
                },
                text = stringResource(R.string.confirm),
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: PanelSupportState,
    onAction: (PanelSupportAction) -> Unit,
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
        if (state.isSupportingListError != null) {
            item("isSupportingListError") {
                CustomCenterTextMessage(
                    modifier = Modifier
                        .animateItem(),
                    text = state.isSupportingListError.asString()
                )
            }
            return@LazyColumn
        }
        if (state.supportingList.isEmpty()) {
            item("isSupportingListEmpty") {
                CustomCenterTextMessage(
                    modifier = Modifier
                        .animateItem(),
                    text = stringResource(R.string.empty)
                )
            }
            return@LazyColumn
        }
        itemsIndexed(
            items = state.supportingList,
            key = { _, supportingModel -> supportingModel.id }
        ) { index, supportingModel ->
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = state.supportingList.size,
                content = {
                    Text(
                        text = supportingModel.profile.username,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = { Text(text = supportingModel.support.toIdr()) },
                trailingContent = {
                    CustomIconButton(
                        onClick = {
                            onAction(PanelSupportAction.ShowSupportBottomSheet(supportingModel))
                        },
                        content = { Icon(Icons.Rounded.Edit, null) }
                    )
                }
            )
        }
    }
}