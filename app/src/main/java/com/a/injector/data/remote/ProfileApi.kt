package com.a.injector.data.remote

import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.Role
import kotlinx.coroutines.flow.Flow

interface ProfileApi {
    fun getProfilesByRole(role: Role): Flow<List<ProfileDto>>
    fun getProfileByHighestContribution(): Flow<List<ProfileDto>>
    fun getProfile(profileId: String): Flow<ProfileDto?>
    suspend fun upsertProfile(profileDto: ProfileDto)
    suspend fun upsertRole(id: String, role: Role)

    fun getTopSupporter(): Flow<List<ProfileDto>>
    fun getTopContributor(): Flow<List<ProfileDto>>

    suspend fun incrementContribution(id: String)
}