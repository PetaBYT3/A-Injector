package com.a.injector.data.local.settings

import com.a.injector.domain.model.state.CommandService
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface SettingsApi {
    val commandService: Flow<CommandService>
    suspend fun setCommandService(commandService: CommandService)

    val language: Flow<Locale>
    suspend fun setLanguage(locale: Locale)
}