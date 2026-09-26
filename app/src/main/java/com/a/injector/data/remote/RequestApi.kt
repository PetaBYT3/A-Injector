package com.a.injector.data.remote

import com.a.injector.data.dto.RequestDto
import kotlinx.coroutines.flow.Flow

interface RequestApi {
    fun getRequests(): Flow<List<RequestDto>>
    fun getRequest(profileId: String): Flow<RequestDto?>
    suspend fun upsertRequest(requestDto: RequestDto)
    suspend fun deleteRequest(requestDto: RequestDto)
}