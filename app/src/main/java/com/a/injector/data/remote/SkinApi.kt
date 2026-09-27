package com.a.injector.data.remote

import com.a.injector.data.dto.SkinDto
import kotlinx.coroutines.flow.Flow

interface SkinApi {
    fun getSingle(skinId: String): Flow<SkinDto?>
    suspend fun upsert(skin: SkinDto)
    suspend fun delete(skin: SkinDto)
}