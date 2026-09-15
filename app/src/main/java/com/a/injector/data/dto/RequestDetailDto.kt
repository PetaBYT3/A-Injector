package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RequestDetailDto(
    val id: String,
    @SerialName("profile_id") val profileId: String? = null,
    val role: Role,
    @SerialName("profile") val profileDto: ProfileDto?
)
