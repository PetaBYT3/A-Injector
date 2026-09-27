@file:OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.HeroDto
import com.a.injector.data.util.SupabaseElement
import com.a.injector.data.util.postgrestActionToUnit
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
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
class HeroApiImpl(
    private val supabaseClient: SupabaseClient
): HeroApi {
    override fun getList(): Flow<List<HeroDto>> {
        val channel = supabaseClient.channel("getHeroDetails:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.HERO_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.SKIN_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.REPLACE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(SupabaseElement.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseElement.HERO_TABLE).select(
                columns = Columns.raw(
                    "*, ${SupabaseElement.SKIN_TABLE}(*, ${SupabaseElement.REPLACE_TABLE}(*))"
                )
            ).decodeList<HeroDto>()
            flowOf(data)
        }
    }

    override fun getSingle(heroId: String): Flow<HeroDto?> {
        val channel = supabaseClient.channel("getHeroDetail:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.HERO_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.SKIN_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseElement.SCHEMA,
                filter = { table = SupabaseElement.REPLACE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(SupabaseElement.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseElement.HERO_TABLE).select(
                request = { filter { eq("id", heroId) } },
                columns = Columns.raw(
                    "*, ${SupabaseElement.SKIN_TABLE}(*, ${SupabaseElement.REPLACE_TABLE}(*))"
                )
            ).decodeSingleOrNull<HeroDto>()
            flowOf(data)
        }
    }

    override suspend fun upsert(hero: HeroDto) {
        supabaseClient.from(SupabaseElement.HERO_TABLE).upsert(hero)
    }

    override suspend fun delete(hero: HeroDto) {
        supabaseClient.from(SupabaseElement.HERO_TABLE).delete(
            request = { filter { HeroDto::id eq hero.id } }
        )
    }
}