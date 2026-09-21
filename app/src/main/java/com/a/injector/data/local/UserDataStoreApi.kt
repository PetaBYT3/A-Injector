package com.a.injector.data.local

import com.a.injector.domain.model.state.CommandService
import kotlinx.coroutines.flow.Flow

interface UserDataStoreApi {
    val commandService: Flow<CommandService>
    suspend fun setCommandService(commandService: CommandService)
}