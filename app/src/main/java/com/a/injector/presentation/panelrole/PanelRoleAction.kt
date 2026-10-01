package com.a.injector.presentation.panelrole

import com.a.injector.domain.model.RoleModel

sealed interface PanelRoleAction {
    data class ShowGrantRequestBottomSheet(val request: RoleModel): PanelRoleAction
    data object DismissGrantRequestBottomSheet: PanelRoleAction
    data object DenyButton: PanelRoleAction
    data object ConfirmButton: PanelRoleAction
}