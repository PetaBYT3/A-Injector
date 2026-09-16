package com.a.injector.data.system

import kotlinx.coroutines.flow.Flow

interface SuperuserCommandService {
    val isGranted: Flow<Boolean>
    suspend fun check()
    suspend fun copy(sourcePath: String, targetPath: String)
}