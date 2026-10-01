package com.a.injector.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.presentation.account.ManageAccount.ChangePassword
import com.a.injector.presentation.account.Profile.Contribution
import com.a.injector.presentation.account.Profile.Email
import com.a.injector.presentation.account.Profile.ProfileRole
import com.a.injector.presentation.account.Profile.Supporting
import com.a.injector.presentation.account.Profile.Username
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.util.ScreenEffectLauncher
import com.a.injector.presentation.util.toIdr
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
        state = AccountState(
            isProfileLoading = false,
            profile = ProfileModel.EMPTY.copy(
                role = Role.Administrator
            )
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
        title = stringResource(R.string.sign_out),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.sign_out_desc))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(AccountAction.SignOutBottomSheet)
                    onAction(AccountAction.SignOutButton)
                },
                text = stringResource(R.string.confirm),
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
        if (state.authState != AuthResult.Guest) {
            itemsIndexed(
                items = profiles,
                key = { _, staticModel -> staticModel.id.name }
            ) { index, staticModel ->
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = profiles.size,
                    leadingContent = staticModel.leadingContent,
                    content = { Text(text = stringResource(staticModel.content)) },
                    supportingContent = {
                        val supportingText = when (staticModel.id) {
                            Email -> state.userInfo?.email ?: stringResource(R.string.unknown)
                            Username -> state.profile.username
                            Contribution -> buildString {
                                append(state.profile.contribution)
                                append(" ")
                                append(stringResource(R.string.top_contributor_desc))
                            }
                            Supporting -> state.profile.nominal.toIdr()
                            ProfileRole -> state.profile.role.title.asString()
                        }
                        Text(text = supportingText)
                    },
                    trailingContent = {
                        when (staticModel.id) {
                            Username -> {
                                CustomIconButton(
                                    onClick = { navBackStack.add(MainNavigationRoute.UsernameScreen) },
                                    content = { Icon(Icons.Rounded.Edit, null) }
                                )
                            }
                            Supporting -> {
                                CustomIconButton(
                                    onClick = { navBackStack.add(MainNavigationRoute.SupportScreen) },
                                    content = { Icon(Icons.Rounded.Edit, null) }
                                )
                            }
                            ProfileRole -> {
                                CustomIconButton(
                                    onClick = { navBackStack.add(MainNavigationRoute.RoleScreen) },
                                    content = { Icon(Icons.Rounded.Edit, null) }
                                )
                            }
                            else -> {}
                        }
                    }
                )
            }
            spacer()
            itemsIndexed(
                items = manageAccounts,
                key = { _, staticModel -> staticModel.id.name }
            ) { index, staticModel ->
                DefaultClickableListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = manageAccounts.size,
                    onClick = {
                        when (staticModel.id) {
                            ChangePassword -> {
                                navBackStack.add(MainNavigationRoute.PasswordScreen)
                            }
                        }
                    },
                    leadingContent = staticModel.leadingContent,
                    content = { Text(text = stringResource(staticModel.content)) },
                    supportingContent = if (staticModel.supportingContent != null) {
                        { Text(text = stringResource(staticModel.supportingContent)) }
                    } else null
                )
            }
        } else {
            item("guestAccount") {
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    content = { Text(text = stringResource(R.string.guest_account)) }
                )
            }
        }
        spacer()
        item("signOutButton") {
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onAction(AccountAction.SignOutBottomSheet) },
                text = stringResource(R.string.sign_out),
                isError = true
            )
        }
    }
}