package com.a.injector.data.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SupporterDto(
    val id: String = Uuid.random().toString(),
    val nominal: Long = 0,

    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val profile: ProfileDto = ProfileDto()
)
