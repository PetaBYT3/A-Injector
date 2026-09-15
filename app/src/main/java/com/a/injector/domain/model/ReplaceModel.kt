package com.a.injector.domain.model

data class ReplaceModel(
    val id: String,
    val skinId: String,
    val label: String,
    val name: String,
    val lastUpdate: Long?,
    val fileSize: Long?
) {
    companion object {
        val EMPTY = ReplaceModel(
            id = "",
            skinId = "",
            label = "",
            name = "",
            lastUpdate = null,
            fileSize = null
        )
    }
}
