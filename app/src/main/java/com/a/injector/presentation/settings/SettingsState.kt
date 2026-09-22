package com.a.injector.presentation.settings

import java.util.Locale

data class SettingsState(
    val currentLanguage: Locale = Locale.US,
    val isLanguageBottomSheetVisible: Boolean = false
)
