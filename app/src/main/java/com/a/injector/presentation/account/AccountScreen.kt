package com.a.injector.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilledTonalButton
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
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.state.RequestState
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.util.CustomBottomSheet
import com.a.injector.presentation.util.CustomButton
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomSurfaceText
import com.a.injector.presentation.util.CustomTextListTitle
import com.a.injector.presentation.util.CustomTopAppBar
import com.a.injector.presentation.util.SnackBarEffectLauncher
import com.a.injector.presentation.util.spacer
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: AccountViewModel = koinViewModel()
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
        state = AccountState(
            isProfileLoading = false,
            profile = ProfileModel.EMPTY.copy(
                role = Role.Administrator
            ),
            requestState = RequestState.Applied
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: AccountState,
    onAction: (AccountAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.account)
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
        visible = state.isSignOutBottomSheetVisible,
        onDismiss = { onAction(AccountAction.SignOutBottomSheet) },
        title = stringResource(R.string.account_sign_out),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.account_sign_out_msg))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(AccountAction.SignOutBottomSheet)
                    onAction(AccountAction.SignOutButton)
                },
                text = stringResource(R.string.account_sign_out_confirm),
                isError = true
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: AccountState,
    onAction: (AccountAction) -> Unit
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

        if (state.isGuestAccount) {
            item("guestAccount") {
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    content = { Text(text = "Guest Account") }
                )
            }
        } else {
            if (state.profile.role == Role.Administrator) {
                item("roleManager") {
                    DefaultClickableListItem(
                        modifier = Modifier
                            .animateItem(),
                        onClick = { navBackStack.add(NavigationRoute.ManageRoleScreen) },
                        content = { Text(text = stringResource(R.string.account_administrator_panel)) },
                        supportingContent = {
                            Text(text = stringResource(R.string.account_administrator_panel_desc))
                        }
                    )
                }
                spacer()
            }

            item("profileTitle") {
                CustomTextListTitle(
                    modifier = Modifier
                        .animateItem(),
                    text = stringResource(R.string.account_profile_title)
                )
            }
            itemsIndexed(
                items = profileAccountItems,
                key = { _, staticModel -> staticModel.id.name }
            ) { index, staticModel ->
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = profileAccountItems.size,
                    content = { Text(text = stringResource(staticModel.contentTextResId)) },
                    supportingContent = {
                        val supportingText = when (staticModel.id) {
                            ProfileAccountId.Username -> state.profile.username
                            ProfileAccountId.Contribution -> state.profile.contribution.toString()
                            ProfileAccountId.Role -> state.profile.role.name
                        }
                        Text(text = supportingText)
                    },
                    trailingContent = if (staticModel.id == ProfileAccountId.Role && state.profile.role == Role.User) {
                        {
                            FilledTonalButton(
                                onClick = { onAction(AccountAction.RequestContributorButton) },
                                enabled = state.requestState == RequestState.NotApplied,
                                content = {
                                    Text(
                                        text = when (state.requestState) {
                                            RequestState.Applied -> "Requested"
                                            RequestState.NotApplied -> "Apply Contributor"
                                        }
                                    )
                                }
                            )
                        }
                    } else null
                )
            }
            spacer()
            item("manageTitle") {
                CustomTextListTitle(
                    modifier = Modifier
                        .animateItem(),
                    text = stringResource(R.string.account_manage_title)
                )
            }
            itemsIndexed(
                items = manageAccountItems,
                key = { _, staticModel -> staticModel.id.name }
            ) { index, staticModel ->
                DefaultClickableListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = manageAccountItems.size,
                    onClick = {
                        when (staticModel.id) {
                            ManageAccountId.ChangePassword -> {}
                            ManageAccountId.DeleteAccount -> {}
                        }
                    },
                    content = { Text(text = stringResource(staticModel.contentTextResId)) },
                    supportingContent = if (staticModel.supportingTextResId != null) {
                        { Text(text = stringResource(staticModel.supportingTextResId)) }
                    } else null
                )
            }
        }
        spacer()
        item("signOutButton") {
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onAction(AccountAction.SignOutBottomSheet) },
                text = "Sign Out",
                isError = true
            )
        }
    }
}