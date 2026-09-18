@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.repository

import android.content.Context
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.Bucket
import com.a.injector.data.local.CommandService
import com.a.injector.data.local.UserDataStoreApi
import com.a.injector.data.remote.StorageApi
import com.a.injector.data.system.ShizukuCommandService
import com.a.injector.data.system.SuperuserCommandService
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.CommandServiceModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.state.Directory
import com.a.injector.domain.repository.DirectoryRepository
import com.a.injector.domain.repository.InjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single
import java.io.File
import java.util.zip.ZipFile

@Single
class InjectRepositoryImpl(
    private val context: Context,
    private val userDataStoreApi: UserDataStoreApi,
    private val storageApi: StorageApi,
    private val shizukuCommandService: ShizukuCommandService,
    private val superuserCommandService: SuperuserCommandService,
    private val directoryRepository: DirectoryRepository
): InjectRepository {
    private companion object {
        private const val TARGET_PATH = "/storage/emulated/0/Android/data/com.mobile.legends/files/dragon2017/assets"
        private val TARGET_FOLDERS = setOf("Art", "Audio", "UI")
    }

    private val _commandService = combine(
        userDataStoreApi.commandService,
        shizukuCommandService.isAuthorized,
        superuserCommandService.isGranted
    ) { commandService, isAuthorized, isGranted ->
        when (commandService) {
            CommandService.Shizuku -> {
                CommandServiceModel(
                    name = commandService,
                    isRunning = isAuthorized
                )
            }
            CommandService.Superuser -> {
                CommandServiceModel(
                    name = commandService,
                    isRunning = isGranted
                )
            }
        }
    }
    override val commandService: Flow<CommandServiceModel> = _commandService

    override fun setCommandService(commandService: CommandService): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            userDataStoreApi.setCommandService(
                commandService = commandService
            )
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun start(replaceModel: ReplaceModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            val fileName = "${replaceModel.id}.zip"

            checkFile(fileName, replaceModel.fileSize)
            val extractedDir = extractFile(fileName)
            val currentService = userDataStoreApi.commandService.first()

            extractedDir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    when (currentService) {
                        CommandService.Shizuku -> {
                            shizukuCommandService.copy(file.absolutePath, TARGET_PATH)
                        }
                        CommandService.Superuser -> {
                            superuserCommandService.copy(file.absolutePath, TARGET_PATH)
                        }
                    }
                }
            }

            extractedDir.deleteRecursively()
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun checkFile(fileName: String, expectedSize: Long?) {
        val replaceFile = directoryRepository.setDirectory(Directory.Script, fileName)
        if (replaceFile.length() != expectedSize) {
            storageApi.download(
                fromBucket = Bucket.SCRIPT,
                fileName = fileName,
                outputPath = replaceFile
            )
        }
    }

    private fun extractFile(fileName: String): File {
        val replaceFile = directoryRepository.setDirectory(Directory.Script, fileName)
        val extractDir = directoryRepository.setDirectory(Directory.Extracted, fileName.substringBeforeLast("."))
        extractDir.mkdirs()

        ZipFile(replaceFile).use { zipFile ->
            val entries = zipFile.entries().asSequence().toList()
            val basePath = entries.firstNotNullOfOrNull { entry ->
                val parts = entry.name.split('/')
                val index = parts.indexOfFirst { it in TARGET_FOLDERS }

                when {
                    index == 0 -> ""
                    index > 0 -> parts.take(index).joinToString("/") + "/"
                    else -> null
                }
            } ?: ""

            entries.forEach { entry ->
                if (!entry.name.startsWith(basePath)) return@forEach
                val relativeName = entry.name.removePrefix(basePath)
                if (relativeName.isEmpty()) return@forEach

                val targetFile = File(extractDir, relativeName)

                if (!targetFile.canonicalPath.startsWith(extractDir.canonicalPath + File.separator)) {
                    return@forEach
                }

                if (entry.isDirectory) {
                    targetFile.mkdirs()
                } else {
                    targetFile.parentFile?.mkdirs()
                    zipFile.getInputStream(entry).use { inputStream ->
                        targetFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        }
        return extractDir
    }
}