package com.a.injector.data.remote.support

import kotlinx.coroutines.flow.Flow

interface SupportApi {
    fun getList(): Flow<List<SupportDto>>
    fun getSingle(profileId: String): Flow<SupportDto?>
    suspend fun upsert(supportDto: SupportDto)
    suspend fun delete(supportDto: SupportDto)
}