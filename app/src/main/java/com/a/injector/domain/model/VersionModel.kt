package com.a.injector.domain.model

data class VersionModel(
    val id: String,
    val platform: String,
    val version: Int,
    val maintenance: Boolean
) {
    companion object {
        val EMPTY = VersionModel(
            id = "",
            platform = "",
            version = 0,
            maintenance = false
        )
    }
}
