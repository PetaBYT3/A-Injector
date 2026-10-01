package com.a.injector.data.remote.profile

import com.a.injector.domain.model.state.Role
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