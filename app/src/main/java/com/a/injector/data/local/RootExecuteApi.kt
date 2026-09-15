package com.a.injector.data.local

import org.koin.core.annotation.Single
import java.io.DataOutputStream

@Single
class RootExecuteApi: ExecuteApi {
    override fun copy(sourcePath: String, targetPath: String): Boolean {
        val process = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(process.outputStream)

        return try {
            val command = "cp -rf '$sourcePath' '$targetPath'\n"
            os.writeBytes(command)
            os.writeBytes("exit\n")
            os.flush()

            process.waitFor() == 0
        } catch (e: Exception) {
            false
        } finally {
            os.close()
            process.destroy()
        }
    }
}