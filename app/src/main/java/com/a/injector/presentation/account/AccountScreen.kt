package com.a.injector.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.signup.PasswordRequirement
import com.a.injector.presentation.signup.passwordRequirements
import com.a.injector.presentation.util.ScreenEffectLauncher
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
                var isPasswordVisible by remember {
                    mutableStateOf(false)
                }
                CustomTextField(
                    label = "New Password",
                    value = state.newPasswordTextField,
                    onValueChange = { onAction(AccountAction.NewPasswordTextField(it)) },
                    trailingIcon = {
                        IconToggleButton(
                            checked = isPasswordVisible,
                            onCheckedChange = { isPasswordVisible = it },
                            content = {
                                val imageVector = when (isPasswordVisible) {
                                    true -> Icons.Rounded.VisibilityOff
                                    false -> Icons.Rounded.Visibility
                                }
                                Icon(imageVector, null)
                            }
                        )
                    },
                    visualTransformation = when (isPasswordVisible) {
                        true -> VisualTransformation.None
                        false -> PasswordVisualTransformation()
                    }
                )
            }
            spacer(5.dp)
            itemsIndexed(
                items = passwordRequirements
            ) { _, staticModel ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    val isRequirementMet = when (staticModel.id) {
                        PasswordRequirement.MoreThanEightCharacter -> state.isPasswordMoreThan8Character
                        PasswordRequirement.ContainUppercase -> state.isPasswordContainUppercase
                        PasswordRequirement.ContainNumber -> state.isPasswordContainNumber
                    }
                    val tint = when (isRequirementMet) {
                        true -> MaterialTheme.colorScheme.primary
                        false -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    val imageVector = when (isRequirementMet) {
                        true -> Icons.Rounded.Check
                        false -> Icons.Rounded.Close
                    }
                    Icon(
                        modifier = Modifier
                            .size(20.dp),
                        tint = tint,
                        imageVector = imageVector,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(staticModel.contentTextResId),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(AccountAction.ChangePasswordBottomSheet)
                    onAction(AccountAction.ChangePasswordButton)
                },
                text = stringResource(R.string.action_confirm),
                enabled = state.isPasswordValid
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
            return@LazyColumn
        }
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
                content = { Text(text = stringResource(staticModel.contentTextResId)) },
                supportingContent = {
                    val supportingText = when (staticModel.id) {
                        Profile.Email -> {
                            state.userInfo?.email ?: stringResource(R.string.title_unknown)
                        }
                        Profile.Username -> {
                            state.profile.username
                        }
                        Profile.Contribution -> {
                            "${state.profile.contribution} ${stringResource(R.string.item_files_uploaded)}"
                        }
                        Profile.Role -> {
                            state.profile.role.name
                        }
                    }
                    Text(
                        text = supportingText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    when (staticModel.id) {
                        Profile.Username -> {
                            CustomIconButton(
                                onClick = {
                                    onAction(AccountAction.ShowUpsertProfileBottomSheet(state.profile))
                                },
                                content = { Icon(Icons.Rounded.Edit, null) },
                                isLoading = state.isUpsertProfileButtonLoading
                            )
                        }
                        Profile.Role -> {
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
                items = administratorMenus,
                key = { _, staticModel -> staticModel.id.name }
            ) { index, staticModel ->
                DefaultClickableListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = administratorMenus.size,
                    onClick = {
                        when (staticModel.id) {
                            AdministratorMenu.RoleManager -> {
                                navBackStack.add(NavigationRoute.ManageRoleScreen)
                            }
                            AdministratorMenu.CleanStorage -> {
                                onAction(AccountAction.CleanStorageBottomSheet)
                            }
                        }
                    },
                    leadingContent = staticModel.leadingContent,
                    content = { Text(text = stringResource(staticModel.contentTextResId)) },
                    supportingContent = { Text(text = stringResource(staticModel.supportingTextResId!!)) },
                    trailingContent = {
                        when (staticModel.id) {
                            AdministratorMenu.RoleManager -> {

                            }
                            AdministratorMenu.CleanStorage -> {
                                if (state.isCleanStorageButtonLoading) {
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
                        ManageAccount.ChangePassword -> {
                            onAction(AccountAction.ChangePasswordBottomSheet)
                        }
                    }
                },
                leadingContent = staticModel.leadingContent,
                content = { Text(text = stringResource(staticModel.contentTextResId)) },
                supportingContent = { Text(text = stringResource(staticModel.supportingTextResId!!)) },
                trailingContent = {
                    when (staticModel.id) {
                        ManageAccount.ChangePassword -> {
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