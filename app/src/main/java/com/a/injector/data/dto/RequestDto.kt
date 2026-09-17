package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RequestDto(
    val id: String,
    val role: Role
)
