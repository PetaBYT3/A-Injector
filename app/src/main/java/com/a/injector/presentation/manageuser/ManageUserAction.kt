package com.a.injector.presentation.manageuser

import com.a.injector.data.dto.Role

sealed interface ManageUserAction {
    data class UsernameTextField(val username: String): ManageUserAction
    data class SupportTextField(val support: String): ManageUserAction
    data class SelectRoleButton(val role: Role): ManageUserAction

    data object UpsertProfileButton: ManageUserAction
}