package com.a.injector.presentation.username

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel

data class UsernameState(
    val isProfileLoadingLoading: Boolean = true,
    val isProfileError: TextResource? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isUpsertProfileButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoadingLoading
}
