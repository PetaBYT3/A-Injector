package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ProfileDto(
    val id: String?,
    val username: String,
    val role: Role,
    val contribution: Int
)
