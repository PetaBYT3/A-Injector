package com.a.injector.presentation.settings

import com.a.injector.domain.model.state.InjectMethod
import java.util.Locale

sealed interface SettingsAction {
    data object CleanCloudStorageBottomSheet: SettingsAction
    data object CleanCloudStorageButton: SettingsAction

    data object InjectMethodBottomSheet: SettingsAction
    data class SetInjectMethodButton(val injectMethod: InjectMethod): SettingsAction

    data object LanguageBottomSheet: SettingsAction
    data class SetLanguageButton(val locale: Locale): SettingsAction

    data object CleanCacheBottomSheet: SettingsAction
    data object CleanCacheButton: SettingsAction

    data object AboutAppBottomSheet: SettingsAction
}