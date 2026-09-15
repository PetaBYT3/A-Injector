package com.a.injector.data.repository

import android.content.pm.PackageManager
import com.a.injector.domain.repository.ShizukuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single
import rikka.shizuku.Shizuku

@Single
class ShizukuRepositoryImpl: ShizukuRepository {

    private val _shizukuState = MutableStateFlow<Boolean>(false)
    override val isAuthorized: StateFlow<Boolean> = _shizukuState

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        if (!Shizuku.pingBinder()) {
            _shizukuState.update { false }
            return@OnBinderReceivedListener
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            _shizukuState.update { true }
        } else {
            _shizukuState.update { false }
        }
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        _shizukuState.update { false }
    }

    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            _shizukuState.update { true }
        } else {
            _shizukuState.update { false }
        }
    }

    init {
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
    }

    override suspend fun check() {
        if (!Shizuku.pingBinder()) {
            _shizukuState.update { false }
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            _shizukuState.update { true }
        } else {
            _shizukuState.update { false }
        }
    }

    override suspend fun copy(sourcePath: String, targetPath: String) {
        val command = arrayOf("cp -rf '$sourcePath' '$targetPath'")
        val newProcess = Shizuku::class.java.getDeclaredMethod(
            "newProcess",
            Array<String>::class.java,
            Array<String>::class.java,
            String::class.java
        )
        newProcess.isAccessible = true
        val process = newProcess.invoke(null, command, null, null) as Process
    }

    override fun destroy() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
    }
}