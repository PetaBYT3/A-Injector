@file:OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.SkinDto
import com.a.injector.data.util.SupabaseConstanta
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class SkinApiImpl(
    private val supabaseClient: SupabaseClient
): SkinApi {
    override fun getSkin(id: String): Flow<SkinDto> {
        return supabaseClient.from(SupabaseConstanta.SKIN_TABLE).selectSingleValueAsFlow(
            primaryKey = SkinDto::id,
            filter = { eq("id", id) }
        )
    }

    override suspend fun upsertSkin(skin: SkinDto) {
        supabaseClient.from(SupabaseConstanta.SKIN_TABLE).upsert(skin)
    }

    override suspend fun deleteSkin(skin: SkinDto) {
        supabaseClient.from(SupabaseConstanta.SKIN_TABLE).delete(
            request = { filter { SkinDto::id eq skin.id } }
        )
    }
}