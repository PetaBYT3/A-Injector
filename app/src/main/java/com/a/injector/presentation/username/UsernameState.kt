package com.a.injector.presentation.username

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.Text

data class UsernameState(
    val isProfileLoadingLoading: Boolean = true,
    val isProfileError: Text? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isUpsertProfileButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoadingLoading
}
