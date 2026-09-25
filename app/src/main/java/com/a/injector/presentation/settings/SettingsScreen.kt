package com.a.injector.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
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
import com.a.injector.data.util.toMegaBytes
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
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
                title = stringResource(R.string.title_settings)
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(2.5.dp)
            ) {
                content(
                    navBackStack = navBackStack,
                    state = state,
                    onAction = onAction
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    )

    CustomBottomSheet(
        visible = state.isLanguageBottomSheetVisible,
        onDismiss = { onAction(SettingsAction.LanguageBottomSheet) },
        title = stringResource(R.string.item_language),
        content = {
            itemsIndexed(
                items = settingsSupportedLanguage,
                key = { _, locale -> locale.toLanguageTag() }
            ) { index, locale ->
                DefaultClickableListItem(
                    modifier = Modifier
                        .animateItem(),
                    index = index,
                    count = settingsSupportedLanguage.size,
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
        visible = state.isClearCacheBottomSheetVisible,
        onDismiss = { onAction(SettingsAction.CleanCacheBottomSheet) },
        title = stringResource(R.string.action_clear),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.lorem_ipsum))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(SettingsAction.CleanCacheBottomSheet)
                    onAction(SettingsAction.CleanCacheButton)
                },
                text = stringResource(R.string.action_clean)
            )
        }
    )
}

private fun LazyListScope.content(
    navBackStack: NavBackStack<NavKey>,
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    itemsIndexed(
        items = settingsMenuItem,
        key = { _, staticModel -> staticModel.id.name }
    ) { index, staticModel ->
        DefaultClickableListItem(
            modifier = Modifier
                .animateItem(),
            index = index,
            count = settingsMenuItem.size,
            onClick = {
                when (staticModel.id) {
                    SettingsMenuId.Language -> {
                        onAction(SettingsAction.LanguageBottomSheet)
                    }
                    SettingsMenuId.CleanCache -> {
                        onAction(SettingsAction.CleanCacheBottomSheet)
                    }
                }
            },
            leadingContent = {
                val imageVector = when (staticModel.id) {
                    SettingsMenuId.Language -> {
                        Icons.Rounded.Language
                    }
                    SettingsMenuId.CleanCache -> {
                        Icons.Rounded.CleaningServices
                    }
                }
                Icon(imageVector, null)
            },
            content = { Text(text = stringResource(staticModel.contentTextResId)) },
            supportingContent = {
                val currentLanguage = state.currentLanguage
                val text = when (staticModel.id) {
                    SettingsMenuId.Language -> {
                        "${currentLanguage.displayLanguage} - ${currentLanguage.displayCountry}"
                    }
                    SettingsMenuId.CleanCache -> {
                        state.cacheSize.toMegaBytes()
                    }
                }
                Text(text = text)
            }
        )
    }
}