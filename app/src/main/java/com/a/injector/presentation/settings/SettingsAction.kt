package com.a.injector.presentation.settings

import java.util.Locale

sealed interface SettingsAction {
    data object LanguageBottomSheet: SettingsAction
    data class SetLanguageButton(val locale: Locale): SettingsAction
}