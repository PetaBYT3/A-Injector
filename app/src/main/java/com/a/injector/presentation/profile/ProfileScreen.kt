package com.a.injector.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
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
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.state.RequestState
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.util.CustomBottomSheet
import com.a.injector.presentation.util.CustomButton
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomTopAppBar
import com.a.injector.presentation.util.SnackBarEffectLauncher
import com.a.injector.presentation.util.spacer
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: ProfileViewModel = koinViewModel()
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
        state = ProfileState(
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
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.title_profile)
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
        onDismiss = { onAction(ProfileAction.SignOutBottomSheet) },
        title = "Sign Out",
        content = {
            item { Text(text = stringResource(R.string.lorem_ipsum)) }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(ProfileAction.SignOutBottomSheet)
                    onAction(ProfileAction.SignOutButton)
                },
                text = "Confirm",
                isError = true
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
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
                        content = { Text(text = "Manage Role") }
                    )
                }
                spacer()
            }

            val profileItems = 3
            item("usernameItem") {
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = 0,
                    count = profileItems,
                    content = { Text(text = "Username") },
                    supportingContent = { Text(text = state.profile.username) }
                )
            }
            item("contributionItem") {
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = 1,
                    count = profileItems,
                    content = { Text(text = stringResource(R.string.title_contribution)) },
                    supportingContent = { Text(text = state.profile.contribution.toString()) }
                )
            }
            item("roleItem") {
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = 2,
                    count = profileItems,
                    content = { Text(text = stringResource(R.string.title_role)) },
                    supportingContent = { Text(text = state.profile.role.name) },
                    trailingContent = {
                        if (state.profile.role == Role.User) {
                            FilledTonalButton(
                                onClick = { onAction(ProfileAction.RequestContributorButton) },
                                enabled = state.requestState == RequestState.NotApplied
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = when (state.requestState) {
                                            RequestState.Applied -> "Requested"
                                            RequestState.NotApplied -> "Apply Contributor"
                                        }
                                    )
                                    Icon(
                                        modifier = Modifier
                                            .size(ButtonDefaults.IconSize),
                                        imageVector = Icons.Rounded.OpenInNew,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
        spacer()
        item("signOutButton") {
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onAction(ProfileAction.SignOutBottomSheet) },
                text = "Sign Out",
                isError = true
            )
        }
    }
}