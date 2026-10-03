package com.a.injector.domain.model

import com.a.injector.domain.model.state.Role

data class RoleModel(
    val id: String,
    val role: Role,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = RoleModel(
            id = "",
            role = Role.Unknown,
            profile = ProfileModel.EMPTY
        )
    }
}
