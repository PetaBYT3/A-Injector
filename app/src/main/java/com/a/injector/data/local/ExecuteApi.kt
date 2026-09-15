package com.a.injector.data.local

interface ExecuteApi {
    fun copy(sourcePath: String, targetPath: String): Boolean
}