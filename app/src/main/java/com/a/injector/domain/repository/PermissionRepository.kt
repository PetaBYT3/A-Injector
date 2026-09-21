package com.a.injector.domain.repository

import kotlinx.coroutines.flow.Flow

interface PermissionRepository {
    val isManageExternalStorageGranted: Flow<Boolean>
    fun checkPermission()
}