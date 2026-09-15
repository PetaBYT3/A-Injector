package com.a.injector.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface SuperuserRepository {
    val isGranted: StateFlow<Boolean>
    suspend fun check()
    suspend fun copy(sourcePath: String, targetPath: String)
}