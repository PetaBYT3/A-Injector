package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SkinDto(
    val id: String,
    @SerialName("hero_id")
    val heroId: String,
    val name: String,
    val label: String,

    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("replace")
    val replaces: List<ReplaceDto> = emptyList()
)
