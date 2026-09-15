package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class HeroDetailDto(
    val id: String,
    val name: String,
    @SerialName("skin") val skins: List<SkinDetailDto> = emptyList()
)
