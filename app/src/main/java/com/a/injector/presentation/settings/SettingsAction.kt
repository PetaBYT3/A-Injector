package com.a.injector.presentation.settings

import java.util.Locale

sealed interface SettingsAction {
    data object CleanCloudStorageBottomSheet: SettingsAction
    data object CleanCloudStorageButton: SettingsAction

    data object LanguageBottomSheet: SettingsAction
    data class SetLanguageButton(val locale: Locale): SettingsAction

    data object CleanCacheBottomSheet: SettingsAction
    data object CleanCacheButton: SettingsAction
}