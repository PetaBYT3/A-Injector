package com.a.injector.data.local

import com.a.injector.data.dto.Executor
import kotlinx.coroutines.flow.Flow

interface UserDataStoreApi {
    val executor: Flow<Executor>
    suspend fun setExecutor(executor: Executor)
}