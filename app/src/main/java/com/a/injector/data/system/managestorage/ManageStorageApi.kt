package com.a.injector.data.system.managestorage

import java.io.File

interface ManageStorageApi {
    suspend fun copyToAndroidData(sourcePath: File, targetPath: String)
}