@file:OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.a.injector.data.remote.hero

import com.a.injector.data.remote.SupabaseConst
import com.a.injector.data.util.mapPostgrestAction
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
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.HERO_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.SKIN_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.REPLACE_TABLE }
            )
        ).map(::mapPostgrestAction).debounce(SupabaseConst.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConst.HERO_TABLE).select(
                columns = Columns.raw(
                    "*, ${SupabaseConst.SKIN_TABLE}(*, ${SupabaseConst.REPLACE_TABLE}(*))"
                )
            ).decodeList<HeroDto>()
            flowOf(data)
        }
    }

    override fun getSingle(heroId: String): Flow<HeroDto?> {
        val channel = supabaseClient.channel("getHeroDetail:${Uuid.random()}")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.HERO_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.SKIN_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConst.SCHEMA,
                filter = { table = SupabaseConst.REPLACE_TABLE }
            )
        ).map(::mapPostgrestAction).debounce(SupabaseConst.DEBOUNCE).onStart {
            channel.subscribe()
            emit(Unit)
        }.onCompletion {
            supabaseClient.realtime.removeChannel(channel)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConst.HERO_TABLE).select(
                request = { filter { eq("id", heroId) } },
                columns = Columns.raw(
                    "*, ${SupabaseConst.SKIN_TABLE}(*, ${SupabaseConst.REPLACE_TABLE}(*))"
                )
            ).decodeSingleOrNull<HeroDto>()
            flowOf(data)
        }
    }

    override suspend fun upsert(hero: HeroDto) {
        supabaseClient.from(SupabaseConst.HERO_TABLE).upsert(hero)
    }

    override suspend fun delete(hero: HeroDto) {
        supabaseClient.from(SupabaseConst.HERO_TABLE).delete(
            request = { filter { HeroDto::id eq hero.id } }
        )
    }
}