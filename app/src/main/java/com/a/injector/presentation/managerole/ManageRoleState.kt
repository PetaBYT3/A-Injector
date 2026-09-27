package com.a.injector.presentation.managerole

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.RoleModel

data class ManageRoleState(
    val isRequestDetailsLoading: Boolean = true,
    val isRequestDetailsError: TextResource? = null,
    val requestDetails: List<RoleModel> = emptyList(),

    val isGrantRequestBottomSheetVisible: Boolean = false,
    val requestToGrant: RoleModel = RoleModel.EMPTY,
) {
    val isContentLoading: Boolean get() =
        isRequestDetailsLoading
}
