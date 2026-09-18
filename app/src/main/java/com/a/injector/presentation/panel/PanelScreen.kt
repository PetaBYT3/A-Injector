package com.a.injector.presentation.panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.navigation.popBackStack
import com.a.injector.presentation.util.CustomBottomSheet
import com.a.injector.presentation.util.CustomButton
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomCenterTextMessage
import com.a.injector.presentation.util.CustomIconButton
import com.a.injector.presentation.util.CustomSurfaceText
import com.a.injector.presentation.util.CustomTonalButton
import com.a.injector.presentation.util.CustomTopAppBar
import com.a.injector.presentation.util.SnackBarEffectLauncher
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PanelScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: PanelViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val snackBarHostState = remember { SnackbarHostState() }

    Screen(
        navBackStack = navBackStack,
        state = state,
        onAction = onAction,
        snackBarHostState = snackBarHostState
    )

    SnackBarEffectLauncher(
        snackBarHostState = snackBarHostState,
        screenEffect = viewModel.effect
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        navBackStack = rememberNavBackStack(),
        state = PanelState(
            isRequestDetailsLoading = false,
            requestDetails = listOf(
                RequestDetailModel(
                    id = "",
                    role = Role.Contributor,
                    profile = ProfileModel(
                        id = "",
                        username = "previewemail@mail.com",
                        role = Role.Contributor,
                        contribution = 0
                    )
                )
            )
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: PanelState,
    onAction: (PanelRoleAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.panel),
                actions = {
                    CustomIconButton(
                        onClick = { onAction(PanelRoleAction.CleanStorageBottomSheet) },
                        content = { Icon(Icons.Rounded.Delete, null) },
                        isLoading = state.isButtonCleanStorageLoading
                    )
                }
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
        visible = state.isCleanStorageBottomSheetVisible,
        onDismiss = { onAction(PanelRoleAction.CleanStorageBottomSheet) },
        title = stringResource(R.string.panel_clean_cloud_storage),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.panel_clean_cloud_storage_msg))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(PanelRoleAction.CleanStorageBottomSheet)
                    onAction(PanelRoleAction.CleanStorageButton)
                },
                text = stringResource(R.string.panel_clean_cloud_storage_accept)
            )
        }
    )

    CustomBottomSheet(
        visible = state.isGrantRequestBottomSheetVisible,
        onDismiss = { onAction(PanelRoleAction.DismissGrantRequestBottomSheet) },
        title = stringResource(R.string.panel_grant_request),
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
                    onAction(PanelRoleAction.DismissGrantRequestBottomSheet)
                    onAction(PanelRoleAction.GrantRequestButton)
                },
                text = stringResource(R.string.panel_grant_request_accept)
            )
        }
    )

    CustomBottomSheet(
        visible = state.isDetachProfileBottomSheetVisible,
        onDismiss = { onAction(PanelRoleAction.DismissDetachProfileBottomSheet) },
        title = stringResource(R.string.panel_detach_contributor),
        content = {
            item {
                DefaultListItem(
                    content = { Text(text = state.profileToDetach.username) },
                    supportingContent = { Text(text = state.profileToDetach.role.name) }
                )
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(PanelRoleAction.DismissDetachProfileBottomSheet)
                    onAction(PanelRoleAction.DetachProfileButton)
                },
                text = stringResource(R.string.panel_detach_contributor_accept),
                isError = true
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: PanelState,
    onAction: (PanelRoleAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val tabList = listOf(
        "Pending Request",
        Role.Contributor.name
    )
    val pagerState = rememberPagerState(
        pageCount = { tabList.size }
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                items = tabList
            ) { index, tab ->
                FilterChip(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    contentPadding = PaddingValues(all = 0.dp),
                    label = { Text(text = tab) }
                )
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState
        ) { index ->
            when (index) {
                0 -> {
                    PendingRequestPager(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = state,
                        onAction = onAction
                    )
                }
                1 -> {
                    ContributorPager(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = state,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingRequestPager(
    modifier: Modifier = Modifier,
    state: PanelState,
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
        when {
            state.requestDetails.isEmpty() -> {
                item("isRequestDetailsEmpty") {
                    CustomCenterTextMessage(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.title_empty)
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
                                    onAction(PanelRoleAction.ShowGrantRequestBottomSheet(requestDetail))
                                },
                                text = stringResource(R.string.panel_grant_request)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContributorPager(
    modifier: Modifier = Modifier,
    state: PanelState,
    onAction: (PanelRoleAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        if (state.isContributorProfilesLoading) {
            item("isContributorProfileLoading") {
                CustomCenterCircularWavyProgressIndicator(
                    modifier = Modifier
                        .animateItem()
                )
            }
            return@LazyColumn
        }
        when {
            state.contributorProfiles.isEmpty() -> {
                item("isContributorProfileEmpty") {
                    CustomCenterTextMessage(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.title_empty)
                    )
                }
            }
            else -> {
                itemsIndexed(
                    items = state.contributorProfiles,
                    key = { _, grantedRequest -> grantedRequest.id }
                ) { index, grantedRequest ->
                    DefaultListItem(
                        modifier = Modifier
                            .animateItem(),
                        index = index,
                        count = state.contributorProfiles.size,
                        content = { Text(text = grantedRequest.username) },
                        supportingContent = { Text(text = grantedRequest.role.name) },
                        trailingContent = {
                            CustomButton(
                                onClick = {
                                    onAction(PanelRoleAction.ShowDetachProfileBottomSheet(grantedRequest))
                                },
                                text = stringResource(R.string.panel_detach_contributor),
                                isError = true
                            )
                        }
                    )
                }
            }
        }
    }
}