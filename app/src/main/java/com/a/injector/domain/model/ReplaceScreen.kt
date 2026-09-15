package com.a.injector.domain.model

data class ReplaceScreen(
    val id: String,
    val heroId: String,
    val skinId: String,
    val name: String,
    val lastUpdate: String,
    val fileSize: String,
    val isDownloaded: Boolean
)
