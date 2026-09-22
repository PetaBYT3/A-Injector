package com.a.injector.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.a.injector.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.a.injector")
class AppModule {
    @Single
    fun provideDataStore(context: Context): DataStore<Preferences> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = PreferencesSerializer,
                producePath = {
                    context.filesDir.resolve("userDataStore.preferences_pb").absolutePath.toPath()
                }
            )
        )
    }

    @Single
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY,
            builder = {
                install(Realtime)
                install(Auth)
                install(Postgrest)
                install(Storage)

                defaultSerializer = KotlinXSerializer(
                    Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        isLenient = true
                    }
                )
            }
        )
    }
}