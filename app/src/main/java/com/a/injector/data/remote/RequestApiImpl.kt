@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.RequestDetailDto
import com.a.injector.data.dto.RequestDto
import com.a.injector.data.util.SupabaseConstanta
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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

@Single
class RequestApiImpl(
    private val supabaseClient: SupabaseClient
): RequestApi {
    override fun getRequestDetails(): Flow<List<RequestDetailDto>> {
        val channel = supabaseClient.channel("getRequestDetail:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.REQUEST_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.PROFILE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(300.milliseconds).onStart {
            emit(Unit)
            channel.subscribe()
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConstanta.REQUEST_TABLE).select(
                columns = Columns.raw(
                    "*, ${SupabaseConstanta.PROFILE_TABLE}(*)"
                )
            ).decodeList<RequestDetailDto>()
            flowOf(data)
        }
    }

    override fun getRequestDetail(id: String): Flow<RequestDetailDto?> {
        val channel = supabaseClient.channel("getRequestDetail:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.REQUEST_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.PROFILE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(300.milliseconds).onStart {
            emit(Unit)
            channel.subscribe()
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.map {
            supabaseClient.from(SupabaseConstanta.REQUEST_TABLE).select(
                request = { filter { eq("id", id) } },
                columns = Columns.raw(
                    "*, ${SupabaseConstanta.PROFILE_TABLE}(*)"
                )
            ).decodeSingleOrNull<RequestDetailDto>()
        }
    }

    override suspend fun upsertRequest(requestDto: RequestDto) {
        supabaseClient.from(SupabaseConstanta.REQUEST_TABLE).upsert(requestDto)
    }

    override suspend fun deleteRequest(requestDto: RequestDto) {
        supabaseClient.from(SupabaseConstanta.REQUEST_TABLE).delete(
            request = { filter { eq("id", requestDto.id) } }
        )
    }
}