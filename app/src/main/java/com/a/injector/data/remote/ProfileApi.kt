package com.a.injector.data.remote

import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.Role
import kotlinx.coroutines.flow.Flow

interface ProfileApi {
    fun getList(): Flow<List<ProfileDto>>
    fun getSingle(profileId: String): Flow<ProfileDto?>
    suspend fun upsert(profileDto: ProfileDto)
    suspend fun delete(id: String, role: Role)

    fun getListBySupporting(): Flow<List<ProfileDto>>
    fun getListByContributor(): Flow<List<ProfileDto>>

    suspend fun incrementContribution(id: String)
}