@file:OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.a.injector.data.remote.profile

import com.a.injector.data.remote.SupabaseConst
import com.a.injector.data.util.mapPostgrestAction
import com.a.injector.domain.model.state.Role
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.koin.core.annotation.Single
import kotlin.uuid.Uuid

@Single
class ProfileApiImpl(
    private val supabaseClient: SupabaseClient
): ProfileApi {
    override fun getList(): Flow<List<ProfileDto>> {
        return supabaseClient.from(SupabaseConst.PROFILE_TABLE).selectAsFlow(
            primaryKey = ProfileDto::id
        )
    }

    override fun getSingle(profileId: String): Flow<ProfileDto?> {
        return supabaseClient.from(SupabaseConst.PROFILE_TABLE).selectSingleValueAsFlow(
            primaryKey = ProfileDto::id,
            filter = { eq("id", profileId) }
        )
    }

    override suspend fun upsert(profileDto: ProfileDto) {
        supabaseClient.from(SupabaseConst.PROFILE_TABLE).upsert(profileDto)
    }

    override suspend fun delete(id: String, role: Role) {
        supabaseClient.from(SupabaseConst.PROFILE_TABLE).update(
            request = { filter { eq("id", id) } },
            update = { set("role", role.name) }
        )
    }

    override fun getListBySupporting(): Flow<List<ProfileDto>> {
        return supabaseClient.from(SupabaseConst.PROFILE_TABLE).selectAsFlow(
            primaryKey = ProfileDto::id,
            filter = {
                gt("support", 0)
            }
        ).map { profileDtos ->
            profileDtos.sortedByDescending { it.support }.take(10)
        }
    }

    override fun getListByContributor(): Flow<List<ProfileDto>> {
        val channel = supabaseClient.channel("getProfileByHighestContribution:${Uuid.random()}")
        return channel.postgresChangeFlow<PostgresAction>(
            schema = SupabaseConst.SCHEMA,
            filter = { table = SupabaseConst.PROFILE_TABLE }
        ).map(::mapPostgrestAction).debounce(SupabaseConst.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.map {
            supabaseClient.from(SupabaseConst.PROFILE_TABLE).select(
                request = {
                    filter { gt("contribution", 0) }
                    order("contribution", Order.DESCENDING)
                    limit(10)
                }
            ).decodeList<ProfileDto>()
        }
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