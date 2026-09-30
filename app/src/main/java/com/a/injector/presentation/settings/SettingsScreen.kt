package com.a.injector.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.RadioButton
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
import com.a.injector.data.util.toMegaBytes
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.settings.CloudSetting.CleanStorage
import com.a.injector.presentation.settings.CloudSetting.PanelSupporting
import com.a.injector.presentation.settings.CloudSetting.RoleManager
import com.a.injector.presentation.settings.CloudSetting.UserPanel
import com.a.injector.presentation.settings.DeviceSetting.CleanCache
import com.a.injector.presentation.settings.DeviceSetting.Language
import com.a.injector.presentation.util.ScreenEffectLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    SettingsScreen(
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
    SettingsScreen(
        navBackStack = rememberNavBackStack(),
        state = SettingsState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun SettingsScreen(
    navBackStack: NavBackStack<NavKey>,
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.settings)
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
        visible = state.isCleanCloudStorageBottomSheetVisible,
        onDismiss = { onAction(SettingsAction.CleanCloudStorageBottomSheet) },
        title = stringResource(R.string.clean_cloud_storage),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.clean_cloud_storage_desc))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(SettingsAction.CleanCloudStorageBottomSheet)
                    onAction(SettingsAction.CleanCloudStorageButton)
                },
                text = stringResource(R.string.confirm)
            )
        }
    )

    CustomBottomSheet(
        visible = state.isLanguageBottomSheetVisible,
        onDismiss = { onAction(SettingsAction.LanguageBottomSheet) },
        title = stringResource(R.string.language),
        content = {
            itemsIndexed(
                items = supportedLanguage,
                key = { _, locale -> locale.toLanguageTag() }
            ) { index, locale ->
                DefaultClickableListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = supportedLanguage.size,
                    onClick = {
                        if (state.currentLanguage != locale) {
                            onAction(SettingsAction.LanguageBottomSheet)
                            onAction(SettingsAction.SetLanguageButton(locale))
                        }
                    },
                    leadingContent = {
                        RadioButton(
                            selected = state.currentLanguage == locale,
                            onClick = null
                        )
                    },
                    content = { Text(text = locale.displayLanguage) },
                    supportingContent = { Text(text = locale.displayCountry) }
                )
            }
        }
    )

    CustomBottomSheet(
        visible = state.isCleanCacheBottomSheetVisible,
        onDismiss = { onAction(SettingsAction.CleanCacheBottomSheet) },
        title = stringResource(R.string.clean_cache),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.clean_cache_desc))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(SettingsAction.CleanCacheBottomSheet)
                    onAction(SettingsAction.CleanCacheButton)
                },
                text = stringResource(R.string.confirm)
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
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
        if (state.profile.role == Role.Administrator) {
            itemsIndexed(
                items = cloudSettings,
                key = { _, static -> static.id.name }
            ) { index, static ->
                DefaultClickableListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = cloudSettings.size,
                    onClick = {
                        when (static.id) {
                            PanelSupporting -> {
                                navBackStack.add(MainNavigationRoute.PanelSupportingScreen)
                            }
                            RoleManager -> {
                                navBackStack.add(MainNavigationRoute.ManageRoleScreen)
                            }
                            UserPanel -> {
                                navBackStack.add(MainNavigationRoute.PanelUserScreen)
                            }
                            CleanStorage -> {
                                onAction(SettingsAction.CleanCloudStorageBottomSheet)
                            }
                        }
                    },
                    leadingContent = static.leadingContent,
                    content = { Text(text = stringResource(static.content)) },
                    supportingContent = if (static.supportingContent != null) {
                        { Text(text = stringResource(static.supportingContent)) }
                    } else null,
                    trailingContent = {
                        when (static.id) {
                            CleanStorage -> {
                                if (state.isCleanCloudStorageButtonLoading) {
                                    CircularWavyProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                )
            }
            spacer()
        }
        itemsIndexed(
            items = deviceSettings,
            key = { _, staticModel -> staticModel.id.name }
        ) { index, static ->
            DefaultClickableListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = deviceSettings.size,
                onClick = {
                    when (static.id) {
                        Language -> {
                            onAction(SettingsAction.LanguageBottomSheet)
                        }
                        CleanCache -> {
                            onAction(SettingsAction.CleanCacheBottomSheet)
                        }
                    }
                },
                leadingContent = static.leadingContent,
                content = { Text(text = stringResource(static.content)) },
                supportingContent = {
                    val text = when (static.id) {
                        Language -> {
                            val currentLanguage = state.currentLanguage
                            "${currentLanguage.displayLanguage} - ${currentLanguage.displayCountry}"
                        }
                        CleanCache -> {
                            state.cacheSize.toMegaBytes()
                        }
                    }
                    Text(text = text)
                },
                trailingContent = {
                    when (static.id) {
                        Language -> {}
                        CleanCache -> {
                            if (state.isCleanCacheButtonLoading) {
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
}