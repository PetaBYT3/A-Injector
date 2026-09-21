package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import com.a.injector.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class PermissionRepositoryImpl(
    private val context: Context
): PermissionRepository {
    private val _isManageExternalStorageGranted = MutableStateFlow(false)
    override val isManageExternalStorageGranted: Flow<Boolean> = _isManageExternalStorageGranted.asStateFlow()

    override fun checkPermission() {
        _isManageExternalStorageGranted.update {
            Environment.isExternalStorageManager()
        }
    }
}