package com.a.injector.domain.model

import com.a.injector.data.dto.Role

data class RequestDetailModel(
    val id: String,
    val role: Role,
    val profile: ProfileModel
)