package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RoleDto(
    val id: String,
    val role: Role,

    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("profile")
    val profile: ProfileDto? = null
)
