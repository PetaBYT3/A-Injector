package com.a.injector.data.remote

import com.a.injector.data.dto.RoleDto
import kotlinx.coroutines.flow.Flow

interface RoleApi {
    fun getList(): Flow<List<RoleDto>>
    fun getSingle(profileId: String): Flow<RoleDto?>
    suspend fun upsert(roleDto: RoleDto)
    suspend fun delete(roleDto: RoleDto)
}