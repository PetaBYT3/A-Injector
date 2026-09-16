package com.a.injector.data.local

import kotlinx.coroutines.flow.Flow

interface UserDataStoreApi {
    val commandService: Flow<CommandService>
    suspend fun setCommandService(commandService: CommandService)
}