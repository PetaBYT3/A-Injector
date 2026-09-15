package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SkinDetailDto(
    val id: String,
    @SerialName("hero_id") val heroId: String,
    val name: String,
    val label: String,
    @SerialName("replace") val replaces: List<ReplaceDto> = emptyList()
)
