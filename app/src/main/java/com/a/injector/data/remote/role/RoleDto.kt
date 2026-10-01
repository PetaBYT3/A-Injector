package com.a.injector.data.remote.role

import androidx.annotation.Keep
import com.a.injector.data.remote.profile.ProfileDto
import com.a.injector.domain.model.state.Role
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