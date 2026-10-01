package com.a.injector.presentation.manageuser

import com.a.injector.domain.model.state.Role

sealed interface ManageUserAction {
    data class UsernameTextField(val username: String): ManageUserAction
    data class SupportTextField(val support: String): ManageUserAction
    data class SelectRoleButton(val role: Role): ManageUserAction

    data object UpsertProfileButton: ManageUserAction
}