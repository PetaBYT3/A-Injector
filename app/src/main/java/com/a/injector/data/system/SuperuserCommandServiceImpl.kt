package com.a.injector.data.system

import android.content.Context
import com.a.injector.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

@Single
class SuperuserCommandServiceImpl(
    private val context: Context
): SuperuserCommandService {
    private val _isGranted = MutableStateFlow(false)
    override val isGranted: Flow<Boolean> = _isGranted.asStateFlow()

    private var process: Process? = null

    override suspend fun check() {
        withContext(Dispatchers.IO) {
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
    }

    override suspend fun copy(sourcePath: String, targetPath: String) {
        withContext(Dispatchers.IO) {
            if (!_isGranted.value) throw Exception(context.getString(R.string.message_superuser_denied))

            process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process?.outputStream)

            try {
                val command = "cp -rf '$sourcePath' '$targetPath'\n"
                os.writeBytes(command)
                os.writeBytes("exit\n")
                os.flush()

                process?.waitFor()
            } finally {
                os.close()
                process?.destroy()
            }
        }
    }
}