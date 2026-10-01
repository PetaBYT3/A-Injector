package com.a.injector.data.remote.skin

import kotlinx.coroutines.flow.Flow

interface SkinApi {
    fun getSingle(skinId: String): Flow<SkinDto?>
    suspend fun upsert(skin: SkinDto)
    suspend fun delete(skin: SkinDto)
}