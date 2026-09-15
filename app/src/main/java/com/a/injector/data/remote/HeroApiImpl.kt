@file:OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.HeroDto
import com.a.injector.data.dto.HeroDetailDto
import com.a.injector.data.util.SupabaseConstanta
import com.a.injector.data.util.postgrestActionToUnit
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds

@Single
class HeroApiImpl(
    private val supabaseClient: SupabaseClient
): HeroApi {
    override fun getHeroDetails(): Flow<List<HeroDetailDto>> {
        val channel = supabaseClient.channel("heroWithSkin:all")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.HERO_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.SKIN_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.REPLACE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(300.milliseconds).onStart {
            emit(Unit)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConstanta.HERO_TABLE).select(
                columns = Columns.raw(
                    "*, ${SupabaseConstanta.SKIN_TABLE}(*, ${SupabaseConstanta.REPLACE_TABLE}(*))"
                )
            ).decodeList<HeroDetailDto>()
            flowOf(data)
        }
    }

    override fun getHeroDetail(id: String): Flow<HeroDetailDto?> {
        val channel = supabaseClient.channel("heroWithSkin:$id")
        return merge(
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.HERO_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.SKIN_TABLE }
            ),
            channel.postgresChangeFlow<PostgresAction>(
                schema = SupabaseConstanta.SCHEMA,
                filter = { table = SupabaseConstanta.REPLACE_TABLE }
            )
        ).map(::postgrestActionToUnit).debounce(300.milliseconds).onStart {
            emit(Unit)
        }.flatMapLatest {
            val data = supabaseClient.from(SupabaseConstanta.HERO_TABLE).select(
                request = { filter { eq("id", id) } },
                columns = Columns.raw(
                    "*, ${SupabaseConstanta.SKIN_TABLE}(*, ${SupabaseConstanta.REPLACE_TABLE}(*))"
                )
            ).decodeSingleOrNull<HeroDetailDto>()
            flowOf(data)
        }
    }

    override fun getHeroes(): Flow<List<HeroDto>> {
        return supabaseClient.from(SupabaseConstanta.HERO_TABLE).selectAsFlow(
            primaryKey = HeroDto::id
        )
    }

    override fun getHero(id: String): Flow<HeroDto?> {
        return supabaseClient.from(SupabaseConstanta.HERO_TABLE).selectSingleValueAsFlow(
            primaryKey = HeroDto::id,
            filter = { eq("id", id) }
        )
    }

    override suspend fun upsertHero(hero: HeroDto) {
        supabaseClient.from(SupabaseConstanta.HERO_TABLE).upsert(hero)
    }

    override suspend fun deleteHero(hero: HeroDto) {
        supabaseClient.from(SupabaseConstanta.HERO_TABLE).delete(
            request = { filter { HeroDto::id eq hero.id } }
        )
    }
}