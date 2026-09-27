package com.a.injector.data.remote

import com.a.injector.data.dto.SupportingDto
import kotlinx.coroutines.flow.Flow

interface SupportingApi {
    fun getList(): Flow<List<SupportingDto>>
    fun getSingle(profileId: String): Flow<SupportingDto?>
    suspend fun upsert(supportingDto: SupportingDto)
    suspend fun delete(supportingDto: SupportingDto)
}