package com.a.injector.presentation.managerole

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestModel

sealed interface ManageRoleAction {
    data class ShowGrantRequestBottomSheet(val requestModel: RequestModel): ManageRoleAction
    data object DismissGrantRequestBottomSheet: ManageRoleAction
    data object GrantRequestButton: ManageRoleAction

    data class ShowDetachProfileBottomSheet(val profileModel: ProfileModel): ManageRoleAction
    data object DismissDetachProfileBottomSheet: ManageRoleAction
    data object DetachProfileButton: ManageRoleAction
}