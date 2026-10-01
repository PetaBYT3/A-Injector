package com.a.injector.domain.model

import com.a.injector.domain.model.state.Role

data class ProfileModel(
    val id: String,
    val username: String,
    val role: Role,
    val contribution: Int,
    val nominal: Long
) {
    companion object {
        val EMPTY = ProfileModel(
            id = "",
            username = "",
            role = Role.User,
            contribution = 0,
            nominal = 0
        )

        val GUEST = ProfileModel(
            id = "Guest",
            username = "Guest",
            role = Role.User,
            contribution = 0,
            nominal = 0
        )
    }
}
