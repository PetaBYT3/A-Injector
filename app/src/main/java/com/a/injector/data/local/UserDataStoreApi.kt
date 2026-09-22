package com.a.injector.data.local

import com.a.injector.domain.model.state.CommandService
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface UserDataStoreApi {
    val commandService: Flow<CommandService>
    suspend fun setCommandService(commandService: CommandService)

    val language: Flow<Locale>
    suspend fun setLanguage(locale: Locale)
}