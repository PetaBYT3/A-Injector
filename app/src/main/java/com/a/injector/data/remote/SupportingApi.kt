package com.a.injector.data.remote

import com.a.injector.data.dto.SupportDto
import kotlinx.coroutines.flow.Flow

interface SupportingApi {
    fun getList(): Flow<List<SupportDto>>
    fun getSingle(profileId: String): Flow<SupportDto?>
    suspend fun upsert(supportDto: SupportDto)
    suspend fun delete(supportDto: SupportDto)
}