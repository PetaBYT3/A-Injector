package com.a.injector.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.util.CustomBottomSheet
import com.a.injector.presentation.util.CustomTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    Screen(
        navBackStack = navBackStack,
        state = state,
        onAction = onAction
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        navBackStack = rememberNavBackStack(),
        state = SettingsState(),
        onAction = {}
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.title_settings)
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
        }
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
                        onAction(SettingsAction.LanguageBottomSheet)
                        if (state.currentLanguage != locale) {
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
                    }
                },
                content = { Text(text = stringResource(staticModel.contentTextResId)) },
                supportingContent = {
                    val text = when (staticModel.id) {
                        SettingsMenuId.Language -> {
                            val displayLanguage = state.currentLanguage.displayLanguage
                            val displayCountry = state.currentLanguage.displayCountry
                            "$displayLanguage ($displayCountry)"
                        }
                    }
                    Text(text = text)
                }
            )
        }
    }
}