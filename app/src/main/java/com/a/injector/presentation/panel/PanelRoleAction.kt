package com.a.injector.presentation.panel

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel

sealed interface PanelRoleAction {
    data object CleanStorageBottomSheet: PanelRoleAction
    data object CleanStorageButton: PanelRoleAction

    data class ShowGrantRequestBottomSheet(val requestModel: RequestDetailModel): PanelRoleAction
    data object DismissGrantRequestBottomSheet: PanelRoleAction
    data object GrantRequestButton: PanelRoleAction

    data class ShowDetachProfileBottomSheet(val profileModel: ProfileModel): PanelRoleAction
    data object DismissDetachProfileBottomSheet: PanelRoleAction
    data object DetachProfileButton: PanelRoleAction
}