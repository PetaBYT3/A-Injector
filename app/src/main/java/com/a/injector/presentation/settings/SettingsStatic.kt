package com.a.injector.presentation.settings

import com.a.injector.R
import com.a.injector.domain.model.StaticModel
import java.util.Locale

val settingsSupportedLanguage = listOf(
    Locale.US, Locale("id", "ID")
)

enum class SettingsMenuId {
    Language
}

val settingsMenuItem = listOf(
    StaticModel(
        id = SettingsMenuId.Language,
        contentTextResId = R.string.item_language
    )
)