package com.a.injector.domain.repository

import kotlinx.coroutines.flow.Flow

interface ShizukuRepository {
    val isAuthorized: Flow<Boolean>
    suspend fun check()
    suspend fun copy(sourcePath: String, targetPath: String)
    fun destroy()
}