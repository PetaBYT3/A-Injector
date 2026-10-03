package com.a.injector.data.remote.profile

import androidx.annotation.Keep
import com.a.injector.domain.model.state.Role
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ProfileDto(
    val id: String,
    val username: String,
    @EncodeDefault
    val role: Role = Role.User,
    @EncodeDefault
    val contribution: Int = 0,
    @EncodeDefault
    val support: Long = 0L
)