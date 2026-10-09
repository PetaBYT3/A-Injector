package com.a.injector.data.system.directory

import android.content.Context
import com.a.injector.domain.model.state.Directory
import org.koin.core.annotation.Single
import java.io.File

@Single
class DirectoryApiImpl(
    private val context: Context
): DirectoryApi {
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