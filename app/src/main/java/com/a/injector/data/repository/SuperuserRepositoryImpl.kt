package com.a.injector.data.repository

import com.a.injector.domain.repository.SuperuserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

@Single
class SuperuserRepositoryImpl : SuperuserRepository {
    private val _isRooted = MutableStateFlow(false)
    override val isGranted: StateFlow<Boolean> = _isRooted.asStateFlow()

    private var process: Process? = null

    override suspend fun check() {
        val isRooted = withContext(Dispatchers.IO) {
            try {
                process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
                val reader = BufferedReader(InputStreamReader(process?.inputStream))
                val output = reader.readLine()

                process?.waitFor() == 0 && output != null && output.contains("uid=0")
            } catch (e: Exception) {
                false
            } finally {
                process?.destroy()
            }
        }
        _isRooted.update { isRooted }
    }

    override suspend fun copy(sourcePath: String, targetPath: String) {
        withContext(Dispatchers.IO) {
            process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process?.outputStream)

            try {
                val command = "cp -rf '$sourcePath' '$targetPath'\n"
                os.writeBytes(command)
                os.writeBytes("exit\n")
                os.flush()
            } finally {
                os.close()
                process?.destroy()
            }
        }
    }
}