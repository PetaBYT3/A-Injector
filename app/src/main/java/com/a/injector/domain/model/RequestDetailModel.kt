package com.a.injector.domain.model

import com.a.injector.data.dto.Role

data class RequestDetailModel(
    val id: String,
    val role: Role,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = RequestDetailModel(
            id = "",
            role = Role.User,
            profile = ProfileModel.EMPTY
        )
    }
}