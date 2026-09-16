package com.a.injector.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class UserDataStoreApiImpl(
    private val dataStore: DataStore<Preferences>
): UserDataStoreApi {
    private companion object {
        val COMMAND_SERVICES = stringPreferencesKey("executor")
    }

    override val commandService: Flow<CommandService> = dataStore.data.map { preferences ->
        CommandService.valueOf(preferences[COMMAND_SERVICES] ?: CommandService.Shizuku.name)
    }

    override suspend fun setCommandService(commandService: CommandService) {
        dataStore.edit { preferences ->
            preferences[COMMAND_SERVICES] = commandService.name
        }
    }
}