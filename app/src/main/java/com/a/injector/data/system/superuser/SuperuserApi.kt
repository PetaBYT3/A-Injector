package com.a.injector.data.system.superuser

import kotlinx.coroutines.flow.Flow
import java.io.File

interface SuperuserApi {
    val isGranted: Flow<Boolean>
    suspend fun check()
    suspend fun copy(sourcePath: File, targetPath: String)
}