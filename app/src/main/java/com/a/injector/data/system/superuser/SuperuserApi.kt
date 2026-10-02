package com.a.injector.data.system.superuser

import kotlinx.coroutines.flow.Flow

interface SuperuserApi {
    val isGranted: Flow<Boolean>
    suspend fun check()
    suspend fun copy(sourcePath: String, targetPath: String)
}