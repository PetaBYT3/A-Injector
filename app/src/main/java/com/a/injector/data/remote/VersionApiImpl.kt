@file:OptIn(SupabaseExperimental::class)

package com.a.injector.data.remote

import com.a.injector.data.dto.VersionDto
import com.a.injector.data.util.SupabaseConstanta
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class VersionApiImpl(
    private val supabaseClient: SupabaseClient
): VersionApi {
    override fun getVersion(): Flow<VersionDto?> {
        return supabaseClient.from(SupabaseConstanta.VERSION_TABLE).selectSingleValueAsFlow(
            primaryKey = VersionDto::id,
            filter = { eq("platform", "Android") }
        )
    }
}