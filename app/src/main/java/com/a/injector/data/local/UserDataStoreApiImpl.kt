package com.a.injector.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.a.injector.data.dto.Executor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class UserDataStoreApiImpl(
    private val dataStore: DataStore<Preferences>
): UserDataStoreApi {
    private companion object {
        val EXECUTOR = stringPreferencesKey("executor")
    }

    override val executor: Flow<Executor> = dataStore.data.map { preferences ->
        Executor.valueOf(preferences[EXECUTOR] ?: Executor.Shizuku.name)
    }

    override suspend fun setExecutor(executor: Executor) {
        dataStore.edit { preferences ->
            preferences[EXECUTOR] = executor.name
        }
    }
}