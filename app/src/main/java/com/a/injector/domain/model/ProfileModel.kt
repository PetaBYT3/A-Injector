package com.a.injector.domain.model

import com.a.injector.data.dto.Role

data class ProfileModel(
    val id: String,
    val email: String,
    val role: Role,
    val contribution: Int
) {
    companion object {
        val EMPTY = ProfileModel(
            id = "",
            email = "",
            role = Role.User,
            contribution = 0
        )
    }
}
