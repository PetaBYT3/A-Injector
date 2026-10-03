@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.a.injector.data.remote.role

import com.a.injector.data.remote.SupabaseConst
import com.a.injector.data.util.mapPostgrestAction
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.koin.core.annotation.Single
import kotlin.uuid.Uuid

@Single
class RoleApiImpl(
    private val supabaseClient: SupabaseClient
): RoleApi {
    override fun getList(): Flow<List<RoleDto>> {
        val channel = supabaseClient.channel("getRequestDetail:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.ROLE_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.PROFILE_TABLE }
            )
        ).map(::mapPostgrestAction).debounce(SupabaseConst.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConst.ROLE_TABLE).select(
                columns = Columns.raw(
                    "*, ${SupabaseConst.PROFILE_TABLE}(*)"
                )
            ).decodeList<RoleDto>()
            flowOf(data)
        }
    }

    override fun getSingle(profileId: String): Flow<RoleDto?> {
        val channel = supabaseClient.channel("getRequestDetail:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.ROLE_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.PROFILE_TABLE }
            )
        ).map(::mapPostgrestAction).debounce(SupabaseConst.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConst.ROLE_TABLE).select(
                request = { filter { eq("id", profileId) } },
                columns = Columns.raw(
                    "*, ${SupabaseConst.PROFILE_TABLE}(*)"
                )
            ).decodeSingleOrNull<RoleDto>()
            flowOf(data)
        }
    }

    override suspend fun upsert(roleDto: RoleDto) {
        supabaseClient.from(SupabaseConst.ROLE_TABLE).upsert(roleDto)
    }

    override suspend fun delete(roleDto: RoleDto) {
        supabaseClient.from(SupabaseConst.ROLE_TABLE).delete(
            request = { filter { eq("id", roleDto.id) } }
        )
    }
}