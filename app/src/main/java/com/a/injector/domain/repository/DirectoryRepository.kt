package com.a.injector.domain.repository

import com.a.injector.domain.model.state.Directory
import java.io.File

interface DirectoryRepository {
    fun initialize()
    fun getDirectory(directory: Directory): File
    fun setDirectory(directory: Directory, fileName: String): File
}