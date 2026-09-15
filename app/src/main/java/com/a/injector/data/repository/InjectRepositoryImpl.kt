@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.repository

import android.content.Context
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.local.UserDataStoreApi
import com.a.injector.data.dto.Bucket
import com.a.injector.data.dto.Executor
import com.a.injector.data.remote.StorageApi
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.Directory
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.repository.DirectoryRepository
import com.a.injector.domain.repository.InjectRepository
import com.a.injector.domain.repository.SuperuserRepository
import com.a.injector.domain.repository.ShizukuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

@Single
class InjectRepositoryImpl(
    private val context: Context,
    private val userDataStoreApi: UserDataStoreApi,
    private val storageApi: StorageApi,
    private val superuserRepository: SuperuserRepository,
    private val shizukuRepository: ShizukuRepository,
    private val directoryRepository: DirectoryRepository
): InjectRepository {
    private val targetPath = "/storage/emulated/0/Android/data/com.mobile.legends/files/dragon2017/assets"

    override fun start(replaceModel: ReplaceModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            val fileName = "${replaceModel.id}.zip"

            checkFile(fileName = fileName, expectedSize = replaceModel.fileSize)

            val extractedPath = extractFile(fileName = fileName)
            val executor = userDataStoreApi.executor.first()

            extractedPath.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    when (executor) {
                        Executor.Superuser -> {
                            superuserRepository.copy(
                                sourcePath = file.absolutePath,
                                targetPath = targetPath
                            )
                        }
                        Executor.Shizuku -> {
                            shizukuRepository.copy(
                                sourcePath = file.absolutePath,
                                targetPath = targetPath
                            )
                        }
                    }
                }
            }

            extractedPath.delete()
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun checkFile(fileName: String, expectedSize: Long?) {
        val actualSize = directoryRepository.setDirectory(Directory.Script, fileName).length()
        if (expectedSize != actualSize) {
            storageApi.download(
                fromBucket = Bucket.SCRIPT,
                fileName = fileName,
                outputPath = directoryRepository.setDirectory(Directory.Script, fileName)
            )
        }
    }

    private fun extractFile(fileName: String): File {
        val targetFile = directoryRepository.setDirectory(Directory.Script, fileName)
        val targetPath = directoryRepository.setDirectory(Directory.Extracted, File(fileName).nameWithoutExtension)
        if (!targetPath.exists()) targetPath.mkdirs()

        ZipFile(targetFile).use { zipFile ->
            val entries = zipFile.entries().toList()

            var basePath = ""
            for (entry in entries) {
                val parts = entry.name.split("/")
                val targetIndex = parts.indexOfFirst { it == "Art" || it == "Audio" || it == "UI" }

                if (targetIndex != -1) {
                    basePath = if (targetIndex == 0) "" else parts.take(targetIndex).joinToString("/") + "/"
                    break
                }
            }

            for (entry in entries) {
                if (!entry.name.startsWith(basePath)) continue

                val relativeName = entry.name.substring(basePath.length)
                if (relativeName.isEmpty()) continue

                val extractedFile = File(targetPath, relativeName)
                if (!extractedFile.canonicalPath.startsWith(targetPath.canonicalPath + File.separator)) {
                    continue
                }

                if (entry.isDirectory) {
                    extractedFile.mkdirs()
                } else {
                    extractedFile.parentFile?.mkdirs()
                    zipFile.getInputStream(entry).use { inputStream ->
                        FileOutputStream(extractedFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        }
        return targetPath
    }
}