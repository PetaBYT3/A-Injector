@file:OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.Role
import com.a.injector.data.util.SupabaseConstanta
import com.a.injector.data.util.postgrestActionToUnit
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

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

    override fun getProfileByHighestContribution(): Flow<List<ProfileDto>> {
        val channel = supabaseClient.channel("getProfileByHighestContribution:${Uuid.random()}")
        return channel.postgresChangeFlow<PostgresAction>(
            schema = SupabaseConstanta.SCHEMA,
            filter = { table = SupabaseConstanta.PROFILE_TABLE }
        ).map(::postgrestActionToUnit).debounce(300.milliseconds).onStart {
            emit(Unit)
            channel.subscribe()
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConstanta.PROFILE_TABLE).select(
                request = {
                    order("contribution", Order.DESCENDING)
                    limit(10)
                }
            ).decodeList<ProfileDto>()
            flowOf(data)
        }
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
            request = { filter { eq("id", id) } },
            update = { set("role", role.name) }
        )
    }

    override suspend fun incrementContribution(id: String) {
        supabaseClient.postgrest.rpc(
            function = "increment_contribution",
            parameters = buildJsonObject {
                put("profile_id", JsonPrimitive(id))
            }
        )
    }
}