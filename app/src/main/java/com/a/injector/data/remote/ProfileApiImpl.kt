@file:OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.Role
import com.a.injector.data.util.SupabaseConstanta
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class ProfileApiImpl(
    private val supabaseClient: SupabaseClient
): ProfileApi {
    override fun getProfilesByRole(role: Role): Flow<List<ProfileDto>> {
        return supabaseClient.from(SupabaseConstanta.PROFILE_TABLE).selectAsFlow(
            primaryKey = ProfileDto::id,
            filter = { eq("role", role.name) }
        )
    }

    override fun getProfile(id: String): Flow<ProfileDto?> {
        return supabaseClient.from(SupabaseConstanta.PROFILE_TABLE).selectSingleValueAsFlow(
            primaryKey = ProfileDto::id,
            filter = { eq("id", id) }
        )
    }

    override suspend fun upsertProfile(profileDto: ProfileDto) {
        supabaseClient.from(SupabaseConstanta.PROFILE_TABLE).upsert(profileDto)
    }

    override suspend fun upsertRole(id: String,  role: Role) {
        supabaseClient.from(SupabaseConstanta.PROFILE_TABLE).update(
            update = { set("role", role.name) },
            request = { filter { eq("id", id) } }
        )
    }
}