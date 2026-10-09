package com.a.injector.data.system.directory

import com.a.injector.domain.model.state.Directory
import java.io.File

interface DirectoryApi {
    fun initialize()
    fun getDirectory(directory: Directory): File
    fun setDirectory(directory: Directory, fileName: String): File
}