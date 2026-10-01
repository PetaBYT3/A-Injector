package com.a.injector.data.remote.hero

import androidx.annotation.Keep
import com.a.injector.data.remote.skin.SkinDto
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Keep
@Serializable
data class HeroDto(
    val id: String = Uuid.random().toString(),
    val name: String = "",

    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("skin")
    val skins: List<SkinDto> = emptyList()
)