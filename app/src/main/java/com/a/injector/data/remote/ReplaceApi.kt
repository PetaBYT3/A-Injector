package com.a.injector.data.remote

import com.a.injector.data.dto.ReplaceDto
import kotlinx.coroutines.flow.Flow

interface ReplaceApi {
    fun getReplaces(): Flow<List<ReplaceDto>>
    fun getReplace(id: String): Flow<ReplaceDto?>
    suspend fun upsertReplace(replace: ReplaceDto)
    suspend fun deleteReplace(replace: ReplaceDto)
}