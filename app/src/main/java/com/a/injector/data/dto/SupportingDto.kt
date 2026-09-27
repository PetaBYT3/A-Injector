package com.a.injector.data.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SupportingDto(
    val id: String = Uuid.random().toString(),
    val nominal: Long,

    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("profile")
    val profile: ProfileDto? = null
)
