package com.a.injector.presentation.panelrole

import com.a.injector.domain.model.RoleModel
import com.a.injector.domain.model.Text

data class PanelRoleState(
    val isRequestDetailsLoading: Boolean = true,
    val isRequestDetailsError: Text? = null,
    val requestDetails: List<RoleModel> = emptyList(),

    val isActionBottomSheetVisible: Boolean = false,
    val requestToAction: RoleModel = RoleModel.EMPTY,
) {
    val isContentLoading: Boolean get() =
        isRequestDetailsLoading
}
