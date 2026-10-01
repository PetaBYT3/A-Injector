package com.a.injector.data.remote.replace

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ReplaceDto(
    val id: String,
    @SerialName("skin_id")
    val skinId: String,
    val label: String,
    val name: String,
    @SerialName("last_update")
    val lastUpdate: Long?,
    @SerialName("file_size")
    val fileSize: Long?
)