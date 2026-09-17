package com.a.injector.data.repository

import android.content.Context
import com.a.injector.domain.model.state.Directory
import com.a.injector.domain.repository.DirectoryRepository
import org.koin.core.annotation.Single
import java.io.File

@Single
class DirectoryRepositoryImpl(
    private val context: Context
): DirectoryRepository {
    private val rootPath = context.getExternalFilesDir(null)

    override fun initialize() {
        Directory.entries.forEach { directory ->
            val directoryPath = File(rootPath, directory.absoluteName)
            if (!directoryPath.exists()) directoryPath.mkdirs()
        }
    }

    override fun getDirectory(
        directory: Directory
    ): File {
        return File(rootPath, directory.absoluteName)
    }

    override fun setDirectory(
        directory: Directory,
        fileName: String
    ): File {
        return File(File(rootPath, directory.absoluteName), fileName)
    }
}