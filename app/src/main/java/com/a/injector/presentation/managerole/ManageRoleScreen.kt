package com.a.injector.presentation.managerole

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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.a.injector.domain.model.RequestModel
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.navigation.popBackStack
import com.a.injector.presentation.util.CustomBottomSheet
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomCenterTextMessage
import com.a.injector.presentation.util.CustomTextListTitle
import com.a.injector.presentation.util.CustomTopAppBar
import com.a.injector.presentation.util.SnackBarEffectLauncher
import com.a.injector.presentation.util.spacer
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageRoleScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: ManageRoleViewModel = koinViewModel()
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
        state = ManageRoleState(
            isRequestDetailsLoading = false,
            requestDetails = listOf(
                RequestDetailModel(
                    id = "",
                    profileId = "",
                    role = Role.Contributor,
                    profile = ProfileModel(
                        id = "",
                        email = "previewemail@mail.com",
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
    state: ManageRoleState,
    onAction: (ManageRoleAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = "Role"
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
        title = stringResource(R.string.title_request),
        content = {
            item {
                CustomCenterTextMessage(text = stringResource(R.string.lorem_ipsum))
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    onAction(ManageRoleAction.DismissGrantRequestBottomSheet)
                    onAction(ManageRoleAction.GrantRequestButton)
                },
                content = { Text(text = stringResource(R.string.title_grant)) }
            )
        }
    )

    CustomBottomSheet(
        visible = state.isDetachProfileBottomSheetVisible,
        onDismiss = { onAction(ManageRoleAction.DismissDetachProfileBottomSheet) },
        title = "Detach",
        content = {
            item {
                CustomCenterTextMessage(text = stringResource(R.string.lorem_ipsum))
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    onAction(ManageRoleAction.DismissDetachProfileBottomSheet)
                    onAction(ManageRoleAction.DetachProfileButton)
                },
                content = { Text(text = stringResource(R.string.title_grant)) }
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
                        content = { Text(text = requestDetail.profile.email) },
                        supportingContent = { Text(text = requestDetail.role.name) },
                        trailingContent = {
                            FilledTonalIconButton(
                                onClick = {
                                    val requestModel = RequestModel(
                                        id = requestDetail.id,
                                        profileId = requestDetail.profileId,
                                        role = requestDetail.role
                                    )
                                    onAction(ManageRoleAction.ShowGrantRequestBottomSheet(requestModel))
                                },
                                content = { Icon(Icons.Rounded.Check, null) }
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
    state: ManageRoleState,
    onAction: (ManageRoleAction) -> Unit
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
                        content = { Text(text = grantedRequest.email) },
                        supportingContent = { Text(text = grantedRequest.role.name) },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    onAction(ManageRoleAction.ShowDetachProfileBottomSheet(grantedRequest))
                                },
                                content = { Icon(Icons.Rounded.Close, null) }
                            )
                        }
                    )
                }
            }
        }
    }
}