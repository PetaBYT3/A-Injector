package com.a.injector.presentation.panelrole

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.RoleModel

data class PanelRoleState(
    val isRequestDetailsLoading: Boolean = true,
    val isRequestDetailsError: TextResource? = null,
    val requestDetails: List<RoleModel> = emptyList(),

    val isActionBottomSheetVisible: Boolean = false,
    val requestToAction: RoleModel = RoleModel.EMPTY,
) {
    val isContentLoading: Boolean get() =
        isRequestDetailsLoading
}
