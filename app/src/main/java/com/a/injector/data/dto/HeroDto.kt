package com.a.injector.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class HeroDto(
    val id: String,
    val name: String
)
