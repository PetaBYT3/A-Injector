package com.a.injector.presentation.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
import com.a.injector.R
import com.a.injector.domain.model.StaticModel
import java.util.Locale

val settingsSupportedLanguage = listOf(
    Locale.US, Locale("id", "ID")
)

enum class SettingsMenuId {
    Language, CleanCache
}

val settingsMenuItem = listOf(
    StaticModel(
        id = SettingsMenuId.Language,
        leadingContent = { Icon(Icons.Rounded.Language, null) },
        contentTextResId = R.string.item_language
    ),
    StaticModel(
        id = SettingsMenuId.CleanCache,
        leadingContent = { Icon(Icons.Rounded.CleaningServices, null) },
        contentTextResId = R.string.item_clean_cache,
        supportingTextResId = R.string.item_clean_cache_desc
    )
)