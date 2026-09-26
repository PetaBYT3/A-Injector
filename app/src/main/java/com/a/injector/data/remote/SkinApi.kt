package com.a.injector.data.remote

import com.a.injector.data.dto.SkinDto
import kotlinx.coroutines.flow.Flow

interface SkinApi {
    fun getSkin(skinId: String): Flow<SkinDto?>
    suspend fun upsertSkin(skin: SkinDto)
    suspend fun deleteSkin(skin: SkinDto)
}