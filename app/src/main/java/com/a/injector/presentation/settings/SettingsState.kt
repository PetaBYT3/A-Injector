package com.a.injector.presentation.settings

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.state.InjectMethod
import java.util.Locale

data class SettingsState(
    val isProfileLoading: Boolean = true,
    val isProfileError: Text? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isCleanCloudStorageBottomSheetVisible: Boolean = false,
    val isCleanCloudStorageButtonLoading: Boolean = false,

    val injectMethod: InjectMethod = InjectMethod.StorageManager,
    val isInjectMethodBottomSheetVisible: Boolean = false,

    val currentLanguage: Locale = Locale.US,
    val isLanguageBottomSheetVisible: Boolean = false,

    val cacheSize: Long = 0,
    val isCleanCacheBottomSheetVisible: Boolean = false,
    val isCleanCacheButtonLoading: Boolean = false,

    val isAboutAppBottomSheetVisible: Boolean = false
)
