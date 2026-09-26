package com.a.injector.presentation.managerole

import com.a.injector.domain.model.RequestModel

sealed interface ManageRoleAction {
    data class ShowGrantRequestBottomSheet(val request: RequestModel): ManageRoleAction
    data object DismissGrantRequestBottomSheet: ManageRoleAction
    data object GrantRequestButton: ManageRoleAction
}