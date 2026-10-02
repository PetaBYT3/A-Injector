package com.a.injector.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.a.injector.domain.model.state.InjectMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.Locale

@Single
class SettingsApiImpl(
    private val dataStore: DataStore<Preferences>
): SettingsApi {
    private companion object {
        val INJECT_METHOD = stringPreferencesKey("injectMethod")
        val LANGUAGE = stringPreferencesKey("language")
    }

    override val injectMethod: Flow<InjectMethod> = dataStore.data.map { preferences ->
        InjectMethod.valueOf(preferences[INJECT_METHOD] ?: InjectMethod.StorageManager.name)
    }

    override suspend fun setInjectMethod(injectMethod: InjectMethod) {
        dataStore.edit { preferences ->
            preferences[INJECT_METHOD] = injectMethod.name
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