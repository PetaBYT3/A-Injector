package com.a.injector.data.system.shizuku

import kotlinx.coroutines.flow.Flow
import java.io.File

interface ShizukuApi {
    val isAuthorized: Flow<Boolean>
    fun check()
    suspend fun copy(sourcePath: File, targetPath: String)
    fun destroy()
}