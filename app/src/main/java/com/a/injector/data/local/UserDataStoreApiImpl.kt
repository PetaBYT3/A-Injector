package com.a.injector.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.a.injector.domain.model.state.CommandService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.Locale

@Single
class UserDataStoreApiImpl(
    private val dataStore: DataStore<Preferences>
): UserDataStoreApi {
    private companion object {
        val COMMAND_SERVICES = stringPreferencesKey("executor")
        val LANGUAGE = stringPreferencesKey("language")
    }

    override val commandService: Flow<CommandService> = dataStore.data.map { preferences ->
        CommandService.valueOf(preferences[COMMAND_SERVICES] ?: CommandService.StoragePermission.name)
    }

    override suspend fun setCommandService(commandService: CommandService) {
        dataStore.edit { preferences ->
            preferences[COMMAND_SERVICES] = commandService.name
        }
    }

    override val language: Flow<Locale> = dataStore.data.map { preferences ->
        Locale.forLanguageTag(preferences[LANGUAGE] ?: "en-US")
    }

    override suspend fun setLanguage(locale: Locale) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = locale.toLanguageTag()
        }
    }
}