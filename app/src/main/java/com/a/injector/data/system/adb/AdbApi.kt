package com.a.injector.data.system.adb

interface AdbApi {
    suspend fun connect()
    suspend fun executeCommand(command: String): String
    fun disconnect()
}