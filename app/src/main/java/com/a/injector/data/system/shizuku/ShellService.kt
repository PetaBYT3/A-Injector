package com.a.injector.data.system.shizuku

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.a.injector.IShellService
import kotlin.system.exitProcess

@Keep
class ShellService : IShellService.Stub {
    constructor(): super()

    constructor(context: Context): super()

    override fun exec(command: String?): Int {
        val process = ProcessBuilder("sh", "-c", command ?: return -1)
            .redirectErrorStream(true)
            .start()

        val exitCode = process.waitFor()
        return exitCode
    }

    override fun destroy() {
        exitProcess(0)
    }
}