@file:SuppressLint("SdCardPath")
@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.system.shizuku

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.a.injector.BuildConfig
import com.a.injector.IShellService
import com.a.injector.data.util.ShizukuMethodFailed
import com.a.injector.data.util.ShizukuUnauthorized
import com.a.injector.data.util.shellQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout
import org.koin.core.annotation.Single
import rikka.shizuku.Shizuku
import java.io.File
import kotlin.time.Duration.Companion.seconds

@Single
class ShizukuApiImpl: ShizukuApi {
    private companion object {
        const val STORAGE_BASE = "/storage/emulated/0"
    }

    private val _isAuthorized = MutableStateFlow(false)
    override val isAuthorized: Flow<Boolean> = _isAuthorized.asStateFlow()

    private val shellService = MutableStateFlow<IShellService?>(null)
    private val isBinding = MutableStateFlow(false)

    private val receivedListener = Shizuku.OnBinderReceivedListener {
        check()
    }

    private val resultListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        val isAuthorized = grantResult == PackageManager.PERMISSION_GRANTED
        _isAuthorized.update { isAuthorized }

        if (isAuthorized) {
            bindShellService()
        }
    }

    private val deadListener = Shizuku.OnBinderDeadListener {
        _isAuthorized.update { false }
        shellService.update { null }
        isBinding.update { false }
    }

    private val userServiceArgs by lazy {
        Shizuku.UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID, ShellService::class.java.name))
            .daemon(false)
            .processNameSuffix("shellService")
            .version(1)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            isBinding.update { false }
            if (service != null && service.pingBinder()) {
                shellService.update { IShellService.Stub.asInterface(service) }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBinding.update { false }
            shellService.update { null }
        }
    }

    private fun bindShellService() {
        if (isBinding.value || shellService.value != null) return
        isBinding.update { true }
        Shizuku.bindUserService(userServiceArgs, serviceConnection)
    }

    init {
        Shizuku.addBinderReceivedListenerSticky(receivedListener)
        Shizuku.addRequestPermissionResultListener(resultListener)
        Shizuku.addBinderDeadListener(deadListener)
    }

    override fun check() {
        if (Shizuku.pingBinder() && !Shizuku.isPreV11()) {
            val isAuthorized = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            _isAuthorized.update { isAuthorized }

            if (isAuthorized) {
                bindShellService()
            }
        } else {
            _isAuthorized.update { false }
        }
    }

    override suspend fun copy(sourcePath: File, targetPath: String) {
        check()
        if (!_isAuthorized.value) {
            throw ShizukuUnauthorized()
        }

        val shell = withTimeout(5.seconds) {
            shellService.filterNotNull().first()
        }

        val resolvedSourcePath = resolvePath(sourcePath.absolutePath)
        val resolvedTargetPath = resolvePath(targetPath)

        val command = buildString {
            val source = resolvedSourcePath.shellQuote()
            val target = resolvedTargetPath.shellQuote()

            if (sourcePath.isDirectory) {
                append("mkdir -p $target")
                append(" && (cd $source && tar -cf - .) | (cd $target && tar -mxf -)")
            } else {
                val parentFolder = resolvedTargetPath.substringBeforeLast('/').shellQuote()
                append("mkdir -p $parentFolder")
                append(" && cat $source > $target")
            }
        }

        val exitCode = shell.exec(command)
        if (exitCode != 0) {
            throw ShizukuMethodFailed(exitCode.toString())
        }
    }

    private fun resolvePath(path: String): String {
        val cleanPath = path.replace("//", "/")
            .removePrefix("/storage/emulated/0/")
            .removePrefix("storage/emulated/0/")
            .removePrefix("/data/media/0/")
            .removePrefix("data/media/0/")
            .removePrefix("/sdcard/")
            .removePrefix("sdcard/")
            .trimStart('/')

        return "$STORAGE_BASE/$cleanPath"
    }

    override fun destroy() {
        Shizuku.removeBinderReceivedListener(receivedListener)
        Shizuku.removeRequestPermissionResultListener(resultListener)
        Shizuku.removeBinderDeadListener(deadListener)

        val shell = shellService.value
        if (shell != null) {
            shell.destroy()
            Shizuku.unbindUserService(userServiceArgs, serviceConnection, true)
        }
    }
}