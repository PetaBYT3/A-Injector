@file:SuppressLint("SdCardPath")
@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.system.superuser

import android.annotation.SuppressLint
import com.a.injector.data.util.SuperuserDenied
import com.a.injector.data.util.SuperuserMethodFailed
import com.a.injector.data.util.shellQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader

@Single
class SuperuserApiImpl: SuperuserApi {
    private companion object {
        const val STORAGE_BASE = "/data/media/0"
    }

    private val _isGranted = MutableStateFlow(false)
    override val isGranted: Flow<Boolean> = _isGranted.asStateFlow()

    override suspend fun check() {
        var process: Process? = null

        try {
            process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val reader = BufferedReader(InputStreamReader(process?.inputStream))
            val output = reader.readLine()

            val result = process?.waitFor() == 0 && output != null && output.contains("uid=0")
            _isGranted.update { result }
        } catch (e: Exception) {
            _isGranted.update { false }
        } finally {
            process?.destroy()
        }
    }

    override suspend fun copy(sourcePath: File, targetPath: String) {
        check()
        if (!_isGranted.value) {
            throw SuperuserDenied()
        }

        val resolvedSourcePath = resolvePath(sourcePath.absolutePath)
        val resolvedTargetPath = resolvePath(targetPath)

        val command = buildString {
            val source = resolvedSourcePath.shellQuote()
            val target = resolvedTargetPath.shellQuote()

            if (sourcePath.isDirectory) {
                append("mkdir -p $target")
                append(" && cp -rf $source/. $target/")
                append(" && chmod -R 777 $target")
            } else {
                val parentQ = resolvedTargetPath.substringBeforeLast('/').shellQuote()
                append("mkdir -p $parentQ")
                append(" && cp -f $source $target")
                append(" && chmod 777 $parentQ")
            }
        }

        val process = Runtime.getRuntime().exec("su")

        try {
            DataOutputStream(process.outputStream).use { dataOutputStream ->
                dataOutputStream.writeBytes("$command\n")
                dataOutputStream.writeBytes("exit\n")
                dataOutputStream.flush()
            }

            val stdErrOutput = BufferedReader(InputStreamReader(process.errorStream)).readText()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                throw SuperuserMethodFailed(stdErrOutput)
            }
        } finally {
            process.destroy()
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
}