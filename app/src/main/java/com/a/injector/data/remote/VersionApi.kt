package com.a.injector.data.remote

import com.a.injector.data.dto.VersionDto
import kotlinx.coroutines.flow.Flow

interface VersionApi {
    fun getVersion(): Flow<VersionDto?>
}