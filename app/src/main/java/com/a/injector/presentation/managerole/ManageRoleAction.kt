package com.a.injector.presentation.managerole

import com.a.injector.domain.model.RoleModel

sealed interface ManageRoleAction {
    data class ShowGrantRequestBottomSheet(val request: RoleModel): ManageRoleAction
    data object DismissGrantRequestBottomSheet: ManageRoleAction
    data object GrantRequestButton: ManageRoleAction
}