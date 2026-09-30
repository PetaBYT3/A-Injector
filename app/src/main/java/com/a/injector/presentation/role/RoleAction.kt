package com.a.injector.presentation.role

import com.a.injector.data.dto.Role

sealed interface RoleAction {
    data object RoleBottomSheet: RoleAction
    data class RoleButton(val role: Role): RoleAction

    data object UpsertRoleButton: RoleAction
}