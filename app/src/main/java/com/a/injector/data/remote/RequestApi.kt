package com.a.injector.data.remote

import com.a.injector.data.dto.RequestDetailDto
import com.a.injector.data.dto.RequestDto
import kotlinx.coroutines.flow.Flow

interface RequestApi {
    fun getRequestDetails(): Flow<List<RequestDetailDto>>
    suspend fun upsertRequest(requestDto: RequestDto)
    suspend fun deleteRequest(requestDto: RequestDto)
}