package com.a.injector.data.remote.support

import com.a.injector.data.remote.profile.ProfileDto
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SupportDto(
    val id: String = Uuid.random().toString(),
    @EncodeDefault
    val support: Long = 0,
    @SerialName("image_url")
    @EncodeDefault
    val imageUrl: String = "",

    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("profile")
    val profile: ProfileDto? = null
)