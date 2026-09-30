package com.a.injector.presentation.settings

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import java.util.Locale

data class SettingsState(
    val isProfileLoading: Boolean = true,
    val isProfileError: TextResource? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isCleanCloudStorageBottomSheetVisible: Boolean = false,
    val isCleanCloudStorageButtonLoading: Boolean = false,

    val currentLanguage: Locale = Locale.US,
    val isLanguageBottomSheetVisible: Boolean = false,

    val cacheSize: Long = 0,
    val isCleanCacheBottomSheetVisible: Boolean = false,
    val isCleanCacheButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading
}
