package com.a.injector.presentation.role

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RoleModel

data class RoleState(
    val isProfileLoading: Boolean = true,
    val isProfileError: TextResource? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isRequestedRoleLoading: Boolean = true,
    val isRequestedRoleError: TextResource? = null,
    val requestedRole: RoleModel = RoleModel.EMPTY,

    val isUpsertRoleButtonLoading: Boolean = false,
    val isRoleBottomSheetVisible: Boolean = false
) {

    val isContentLoading: Boolean get() =
        isProfileLoading ||
        isRequestedRoleLoading

    val isRequested: Boolean get() = requestedRole.id.isNotBlank()
}
