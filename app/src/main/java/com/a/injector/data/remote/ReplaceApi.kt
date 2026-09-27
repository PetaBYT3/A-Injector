package com.a.injector.data.remote

import com.a.injector.data.dto.ReplaceDto
import kotlinx.coroutines.flow.Flow

interface ReplaceApi {
    fun getList(): Flow<List<ReplaceDto>>
    fun getSingle(replaceId: String): Flow<ReplaceDto?>
    suspend fun upsert(replace: ReplaceDto)
    suspend fun delete(replace: ReplaceDto)
}