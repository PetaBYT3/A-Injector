@file:OptIn(SupabaseExperimental::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.ReplaceDto
import com.a.injector.data.util.SupabaseElement
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class ReplaceApiImpl(
    private val supabaseClient: SupabaseClient
): ReplaceApi {
    override fun getList(): Flow<List<ReplaceDto>> {
        return supabaseClient.from(SupabaseElement.REPLACE_TABLE).selectAsFlow(
            primaryKey = ReplaceDto::id
        )
    }

    override fun getSingle(replaceId: String): Flow<ReplaceDto?> {
        return supabaseClient.from(SupabaseElement.REPLACE_TABLE).selectSingleValueAsFlow(
            primaryKey = ReplaceDto::id,
            filter = { eq("id", replaceId) }
        )
    }

    override suspend fun upsert(replace: ReplaceDto) {
        supabaseClient.from(SupabaseElement.REPLACE_TABLE).upsert(replace)
    }

    override suspend fun delete(replace: ReplaceDto) {
        supabaseClient.from(SupabaseElement.REPLACE_TABLE).delete(
            request = { filter { eq("id", replace.id) } }
        )
    }
}