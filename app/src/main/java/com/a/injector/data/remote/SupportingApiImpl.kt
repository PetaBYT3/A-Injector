@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.SupportDto
import com.a.injector.data.util.SupabaseElement
import com.a.injector.data.util.postgrestActionToUnit
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
class SupportingApiImpl(
    private val supabaseClient: SupabaseClient
): SupportingApi {
    override fun getList(): Flow<List<SupportDto>> {
        val channel = supabaseClient.channel("getList:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.SUPPORT_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.PROFILE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(SupabaseElement.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseElement.SUPPORT_TABLE).select(
                columns = Columns.raw(
                    "*, ${SupabaseElement.PROFILE_TABLE}(*)"
                )
            ).decodeList<SupportDto>()
            flowOf(data)
        }
    }

    override fun getSingle(profileId: String): Flow<SupportDto?> {
        val channel = supabaseClient.channel("getSingle:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.SUPPORT_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.PROFILE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(SupabaseElement.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.map {
            supabaseClient.from(SupabaseElement.SUPPORT_TABLE).select(
                request = { filter { eq("id", profileId) } },
                columns = Columns.raw(
                    "*, ${SupabaseElement.PROFILE_TABLE}(*)"
                )
            ).decodeSingleOrNull<SupportDto>()
        }
    }

    override suspend fun upsert(supportDto: SupportDto) {
        supabaseClient.from(SupabaseElement.SUPPORT_TABLE).upsert(supportDto)
    }

    override suspend fun delete(supportDto: SupportDto) {
        supabaseClient.from(SupabaseElement.SUPPORT_TABLE).delete(
            request = { filter { eq("id", supportDto.id) } }
        )
    }
}