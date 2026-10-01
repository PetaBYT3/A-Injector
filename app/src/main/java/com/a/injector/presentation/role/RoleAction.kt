package com.a.injector.presentation.role

import com.a.injector.domain.model.state.Role

sealed interface RoleAction {
    data object RoleBottomSheet: RoleAction
    data class RoleButton(val role: Role): RoleAction

    data object UpsertRoleButton: RoleAction
}