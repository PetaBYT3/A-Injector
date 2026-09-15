@file:OptIn(SupabaseExperimental::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.ReplaceDto
import com.a.injector.data.util.SupabaseConstanta
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class ReplaceApiImpl(
    private val supabaseClient: SupabaseClient
): ReplaceApi {
    override fun getReplace(id: String): Flow<ReplaceDto?> {
        return supabaseClient.from(SupabaseConstanta.REPLACE_TABLE).selectSingleValueAsFlow(
            primaryKey = ReplaceDto::id,
            filter = { eq("id", id) }
        )
    }

    override suspend fun upsertReplace(replace: ReplaceDto) {
        supabaseClient.from(SupabaseConstanta.REPLACE_TABLE).upsert(replace)
    }

    override suspend fun deleteReplace(replace: ReplaceDto) {
        supabaseClient.from(SupabaseConstanta.REPLACE_TABLE).delete(
            request = { filter { eq("id", replace.id) } }
        )
    }
}