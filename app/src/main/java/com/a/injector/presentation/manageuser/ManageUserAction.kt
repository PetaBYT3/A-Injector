package com.a.injector.presentation.manageuser

import com.a.injector.data.dto.Role

sealed interface ManageUserAction {
    data class NominalToAddTextField(val nominalToAdd: String): ManageUserAction
    data object SelectRoleBottomSheet: ManageUserAction
    data class SelectRoleButton(val role: Role): ManageUserAction

    data object UpsertProfileButton: ManageUserAction
}