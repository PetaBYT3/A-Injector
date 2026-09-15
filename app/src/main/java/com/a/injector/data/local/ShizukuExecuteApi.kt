package com.a.injector.data.local

import org.koin.core.annotation.Single
import rikka.shizuku.Shizuku

@Single
class ShizukuExecuteApi: ExecuteApi {
    override fun copy(sourcePath: String, targetPath: String): Boolean {
        return try {
            val command = arrayOf("cp -rf '$sourcePath' '$targetPath'")
            val newProcess = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcess.isAccessible = true
            val process = newProcess.invoke(null, command, null, null) as Process

            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }
}