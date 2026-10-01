package com.a.injector.data.remote.version

import kotlinx.coroutines.flow.Flow

interface VersionApi {
    fun getSingle(): Flow<VersionDto?>
}