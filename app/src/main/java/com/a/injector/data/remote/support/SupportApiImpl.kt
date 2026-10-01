@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.a.injector.data.remote.support

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
class SupportApiImpl(
    private val supabaseClient: SupabaseClient
): SupportApi {
    override fun getList(): Flow<List<SupportDto>> {
        val channel = supabaseClient.channel("getList:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.SUPPORT_TABLE }
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
            val data = supabaseClient.from(SupabaseConst.SUPPORT_TABLE).select(
                columns = Columns.raw(
                    "*, ${SupabaseConst.PROFILE_TABLE}(*)"
                )
            ).decodeList<SupportDto>()
            flowOf(data)
        }
    }

    override fun getSingle(profileId: String): Flow<SupportDto?> {
        val channel = supabaseClient.channel("getSingle:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.SUPPORT_TABLE }
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
        }.map {
            supabaseClient.from(SupabaseConst.SUPPORT_TABLE).select(
                request = { filter { eq("id", profileId) } },
                columns = Columns.raw(
                    "*, ${SupabaseConst.PROFILE_TABLE}(*)"
                )
            ).decodeSingleOrNull<SupportDto>()
        }
    }

    override suspend fun upsert(supportDto: SupportDto) {
        supabaseClient.from(SupabaseConst.SUPPORT_TABLE).upsert(supportDto)
    }

    override suspend fun delete(supportDto: SupportDto) {
        supabaseClient.from(SupabaseConst.SUPPORT_TABLE).delete(
            request = { filter { eq("id", supportDto.id) } }
        )
    }
}