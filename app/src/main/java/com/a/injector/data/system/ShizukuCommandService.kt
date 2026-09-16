package com.a.injector.data.system

import kotlinx.coroutines.flow.Flow

interface ShizukuCommandService {
    val isAuthorized: Flow<Boolean>
    suspend fun check()
    suspend fun copy(sourcePath: String, targetPath: String)
    suspend fun destroy()
}