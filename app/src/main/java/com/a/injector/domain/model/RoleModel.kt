package com.a.injector.domain.model

import com.a.injector.data.dto.Role

data class RoleModel(
    val id: String,
    val role: Role,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = RoleModel(
            id = "",
            role = Role.User,
            profile = ProfileModel.EMPTY
        )
    }
}
