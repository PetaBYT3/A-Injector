package com.a.injector.data.remote

import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.Role
import kotlinx.coroutines.flow.Flow

interface ProfileApi {
    fun getProfilesByRole(role: Role): Flow<List<ProfileDto>>
    fun getProfile(id: String): Flow<ProfileDto?>
    suspend fun upsertProfile(profileDto: ProfileDto)
    suspend fun upsertRole(id: String, role: Role)
}