package com.a.injector.data.system

import android.content.Context
import android.content.pm.PackageManager
import com.a.injector.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import rikka.shizuku.Shizuku

@Single
class ShizukuCommandServiceImpl(
    private val context: Context
): ShizukuCommandService {
    private val _isAuthorized = MutableStateFlow(false)
    override val isAuthorized: Flow<Boolean> = _isAuthorized.asStateFlow()

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        if (Shizuku.pingBinder()) {
            _isAuthorized.update { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED }
        } else {
            _isAuthorized.update { false }
        }
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        _isAuthorized.update { false }
    }

    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        _isAuthorized.update { grantResult == PackageManager.PERMISSION_GRANTED }
    }

    init {
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
    }

    override suspend fun check() {
        if (Shizuku.pingBinder()) {
            _isAuthorized.update { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED }
        } else {
            _isAuthorized.update { false }
        }
    }

    override suspend fun copy(sourcePath: String, targetPath: String) {
        withContext(Dispatchers.IO) {
            if (!_isAuthorized.value) throw Exception(context.getString(R.string.message_shizuku_unauthorized))

            val command = arrayOf("sh", "-c", "cp -rf '$sourcePath' '$targetPath'")
            val newProcess = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcess.isAccessible = true
            val process = newProcess.invoke(null, command, null, null) as Process
            process.waitFor()
        }
    }

    override suspend fun destroy() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
    }
}