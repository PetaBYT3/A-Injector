package com.a.injector.presentation.manageuser

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel

data class ManageUserState(
    val isProfileLoading: Boolean = true,
    val isProfileError: TextResource? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val nominalToAddTextField: String = "",
    val isSelectRoleBottomSheetVisible: Boolean = false,

    val isUpsertProfileButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading
}