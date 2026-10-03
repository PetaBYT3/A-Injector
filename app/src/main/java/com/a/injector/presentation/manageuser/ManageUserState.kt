package com.a.injector.presentation.manageuser

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.Text

data class ManageUserState(
    val isProfileLoading: Boolean = true,
    val isProfileError: Text? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val supportTextField: String = "",

    val isUpsertProfileButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading
}