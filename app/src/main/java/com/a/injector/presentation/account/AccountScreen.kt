package com.a.injector.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.PermIdentity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.CircularWavyProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTextListTitle
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.ScreenEffectLauncher
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.navigation.NavigationRoute
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
                title = stringResource(R.string.title_account)
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
        title = stringResource(R.string.action_sign_out),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.message_sign_out))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(AccountAction.SignOutBottomSheet)
                    onAction(AccountAction.SignOutButton)
                },
                text = stringResource(R.string.action_confirm),
                isError = true
            )
        }
    )

    CustomBottomSheet(
        visible = state.isUpsertProfileBottomSheetVisible,
        onDismiss = { onAction(AccountAction.DismissUpsertProfileBottomSheet) },
        title = stringResource(R.string.title_profile),
        content = {
            item {
                CustomTextField(
                    label = stringResource(R.string.item_username),
                    value = state.profileToUpsert.username,
                    onValueChange = { onAction(AccountAction.UsernameTextField(it)) }
                )
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(AccountAction.DismissUpsertProfileBottomSheet)
                    onAction(AccountAction.UpsertProfileButton)
                },
                text = stringResource(R.string.action_confirm)
            )
        }
    )

    CustomBottomSheet(
        visible = state.isCleanStorageBottomSheetVisible,
        onDismiss = { onAction(AccountAction.CleanStorageBottomSheet) },
        title = stringResource(R.string.action_clean),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.message_clean))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(AccountAction.CleanStorageBottomSheet)
                    onAction(AccountAction.CleanStorageButton)
                },
                text = stringResource(R.string.action_confirm)
            )
        }
    )

    CustomBottomSheet(
        visible = state.isChangePasswordBottomSheetVisible,
        onDismiss = { onAction(AccountAction.ChangePasswordBottomSheet) },
        title = stringResource(R.string.item_manage),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.message_reset_password))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = { onAction(AccountAction.SendChangePasswordEmailButton) },
                text = stringResource(R.string.action_send)
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

        if (state.profile == ProfileModel.GUEST) {
            item("guestAccount") {
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    content = { Text(text = "Guest Account") }
                )
            }
        } else {
            itemsIndexed(
                items = profileAccountItems,
                key = { _, staticModel -> staticModel.id.name }
            ) { index, staticModel ->
                DefaultListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = profileAccountItems.size,
                    leadingContent = {
                        val imageVector = when (staticModel.id) {
                            ProfileAccountId.Email -> Icons.Rounded.Email
                            ProfileAccountId.Username -> Icons.Rounded.Person
                            ProfileAccountId.Contribution -> Icons.Rounded.Upload
                            ProfileAccountId.Role -> Icons.Rounded.PermIdentity
                        }
                        Icon(
                            imageVector = imageVector,
                            contentDescription = null
                        )
                    },
                    content = { Text(text = stringResource(staticModel.contentTextResId)) },
                    supportingContent = {
                        val supportingText = when (staticModel.id) {
                            ProfileAccountId.Email -> state.userInfo?.email ?: ""
                            ProfileAccountId.Username -> state.profile.username
                            ProfileAccountId.Contribution -> "${state.profile.contribution} ${stringResource(R.string.item_files_uploaded)}"
                            ProfileAccountId.Role -> state.profile.role.name
                        }
                        Text(
                            text = supportingText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingContent = {
                        when (staticModel.id) {
                            ProfileAccountId.Username -> {
                                CustomIconButton(
                                    onClick = {
                                        onAction(AccountAction.ShowUpsertProfileBottomSheet(state.profile))
                                    },
                                    content = { Icon(Icons.Rounded.Edit, null) },
                                    isLoading = state.isUpsertProfileButtonLoading
                                )
                            }
                            ProfileAccountId.Role -> {
                                if (state.profile.role == Role.User) {
                                    FilledTonalButton(
                                        onClick = { onAction(AccountAction.RequestContributorButton) },
                                        enabled = state.requestState == RequestState.NotApplied,
                                        content = {
                                            val text = when (state.requestState) {
                                                RequestState.Applied -> {
                                                    stringResource(R.string.action_requested)
                                                }
                                                RequestState.NotApplied -> {
                                                    stringResource(R.string.action_apply_contributor)
                                                }
                                            }
                                            Text(text = text)
                                        }
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                )
            }
            spacer()
            if (state.profile.role == Role.Administrator) {
                item("administratorTitle") {
                    CustomTextListTitle(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.item_admin_menu)
                    )
                }
                itemsIndexed(
                    items = profileAdministratorMenuItems,
                    key = { _, staticModel -> staticModel.id.name }
                ) { index, staticModel ->
                    DefaultClickableListItem(
                        modifier = Modifier
                            .animateItem(),
                        index = index,
                        count = profileAdministratorMenuItems.size,
                        onClick = {
                            when (staticModel.id) {
                                AdministratorMenuId.RoleManager -> {
                                    navBackStack.add(NavigationRoute.ManageRoleScreen)
                                }
                                AdministratorMenuId.CleanStorage -> {
                                    onAction(AccountAction.CleanStorageBottomSheet)
                                }
                            }
                        },
                        leadingContent = {
                            val imageVector = when (staticModel.id) {
                                AdministratorMenuId.RoleManager -> Icons.Rounded.AdminPanelSettings
                                AdministratorMenuId.CleanStorage -> Icons.Rounded.CleaningServices
                            }
                            Icon(imageVector, null)
                        },
                        content = { Text(text = stringResource(staticModel.contentTextResId)) },
                        supportingContent = { Text(text = stringResource(staticModel.supportingTextResId!!)) },
                        trailingContent = {
                            if (staticModel.id == AdministratorMenuId.CleanStorage) {
                                if (state.isCleanStorageButtonLoading) {
                                    CircularWavyProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                }
                            }
                        }
                    )
                }
                spacer()
            }
            item("manageTitle") {
                CustomTextListTitle(
                    modifier = Modifier
                        .animateItem(),
                    text = stringResource(R.string.item_manage)
                )
            }
            itemsIndexed(
                items = profileManageAccountItems,
                key = { _, staticModel -> staticModel.id.name }
            ) { index, staticModel ->
                DefaultClickableListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = profileManageAccountItems.size,
                    onClick = {
                        when (staticModel.id) {
                            ManageAccountId.ChangePassword -> {
                                onAction(AccountAction.ChangePasswordBottomSheet)
                            }
                        }
                    },
                    leadingContent = {
                        val imageVector = when (staticModel.id) {
                            ManageAccountId.ChangePassword -> Icons.Rounded.Password
                        }
                        Icon(imageVector, null)
                    },
                    content = { Text(text = stringResource(staticModel.contentTextResId)) },
                    supportingContent = if (staticModel.supportingTextResId != null) {
                        { Text(text = stringResource(staticModel.supportingTextResId)) }
                    } else null,
                    trailingContent = {
                        when (staticModel.id) {
                            ManageAccountId.ChangePassword -> {
                                if (state.isChangePasswordButtonLoading) {
                                    CircularWavyProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
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
                onClick = { onAction(AccountAction.SignOutBottomSheet) },
                text = "Sign Out",
                isError = true
            )
        }
    }
}