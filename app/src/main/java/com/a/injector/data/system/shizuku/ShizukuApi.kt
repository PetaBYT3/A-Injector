package com.a.injector.data.system.shizuku

import kotlinx.coroutines.flow.Flow

interface ShizukuApi {
    val isAuthorized: Flow<Boolean>
    suspend fun check()
    suspend fun copy(sourcePath: String, targetPath: String)
    suspend fun destroy()
}