package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Keep
@Serializable
data class ProfileDto(
    val id: String = Uuid.random().toString(),
    val username: String = "",
    val role: Role = Role.User,
    val contribution: Int = 0,
    val support: Long = 0L
)
