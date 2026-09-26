package com.a.injector.domain.model

import com.a.injector.data.dto.Role

data class RequestModel(
    val id: String,
    val role: Role,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = RequestModel(
            id = "",
            role = Role.User,
            profile = ProfileModel.EMPTY
        )
    }
}
