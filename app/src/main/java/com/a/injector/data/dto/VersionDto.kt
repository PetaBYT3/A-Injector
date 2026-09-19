package com.a.injector.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VersionDto(
    val id: String,
    val platform: String,
    val version: Int,
    val maintenance: Boolean
)
