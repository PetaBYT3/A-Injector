@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.local.settings.SettingsApi
import com.a.injector.data.remote.storage.StorageApi
import com.a.injector.data.system.managestorage.ManageStorageApi
import com.a.injector.data.system.shizuku.ShizukuApi
import com.a.injector.data.system.superuser.SuperuserApi
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.InjectModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.state.Bucket
import com.a.injector.domain.model.state.InjectMethod.Shizuku
import com.a.injector.domain.model.state.InjectMethod.StorageManager
import com.a.injector.domain.model.state.InjectMethod.Superuser
import com.a.injector.domain.repository.InjectRepository
import com.a.injector.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import java.io.File
import java.util.zip.ZipFile

@Single
class InjectRepositoryImpl(
    private val context: Context,
    private val settingsApi: SettingsApi,
    private val permissionRepository: PermissionRepository,
    private val manageStorageApi: ManageStorageApi,
    private val shizukuApi: ShizukuApi,
    private val superuserApi: SuperuserApi,
    private val storageApi: StorageApi
): InjectRepository {
    companion object {
        private const val MANAGE_STORAGE_TARGET_PATH = "com.mobile.legends/files/dragon2017/assets/"
        private const val SHELL_TARGET_PATH = "/storage/emulated/0/Android/data/com.mobile.legends/files/dragon2017/assets/"
    }

    override val currentInjectMethod: Flow<InjectModel> = combine(
        settingsApi.injectMethod,
        permissionRepository.isManageExternalStorageGranted,
        shizukuApi.isAuthorized,
        superuserApi.isGranted
    ) { injectMethod, permission, authorized, granted ->
        InjectModel(
            injectMethod = injectMethod,
            isGranted = when (injectMethod) {
                StorageManager -> permission
                Shizuku -> authorized
                Superuser -> granted
            }
        )
    }

    override fun execute(replaceModel: ReplaceModel): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            val fileName = "${replaceModel.id}.zip"

            emit(Either.Right(Text.Resource(R.string.downloading)))
            val replaceFile = validateReplace(fileName, replaceModel.fileSize)

            emit(Either.Right(Text.Resource(R.string.extracting)))
            val extractedReplace = extractAssets(replaceFile)

            emit(Either.Right(Text.Resource(R.string.copying)))
            val injectMethod = settingsApi.injectMethod.first()

            when (injectMethod) {
                StorageManager -> {
                    manageStorageApi.copyToAndroidData(
                        sourcePath = extractedReplace,
                        targetPath = MANAGE_STORAGE_TARGET_PATH
                    )
                }
                Shizuku -> {
                    extractedReplace.listFiles()?.forEach { file ->
                        shizukuApi.copy(
                            sourcePath = file.absolutePath,
                            targetPath = SHELL_TARGET_PATH
                        )
                    }
                }
                Superuser -> {
                    extractedReplace.listFiles()?.forEach { file ->
                        superuserApi.copy(
                            sourcePath = file.absolutePath,
                            targetPath = SHELL_TARGET_PATH
                        )
                    }
                }
            }
            extractedReplace.deleteRecursively()
        }.catchAndDispatch()
    }

    private suspend fun validateReplace(fileName: String, expectedSize: Long?): File {
        val publicDownloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetDownloadPath = File(publicDownloadPath, context.getString(R.string.app_name))

        if (!targetDownloadPath.exists()) {
            targetDownloadPath.mkdirs()
        }

        val downloadedFile = File(targetDownloadPath, fileName)
        if (!downloadedFile.exists() || downloadedFile.length() != expectedSize) {
            storageApi.download(
                fromBucket = Bucket.SCRIPT,
                fileName = fileName,
                outputPath = downloadedFile
            )
        }
        return downloadedFile
    }

    private fun extractAssets(targetFile: File): File {
        val targetPath = File(targetFile.parentFile, targetFile.nameWithoutExtension)
        targetPath.mkdirs()

        val canonicalTargetPath = targetPath.canonicalPath + File.separator

        ZipFile(targetFile).use { zipFile ->
            val entries = zipFile.entries().asSequence().toList()
            val assetsPrefix = entries.firstNotNullOfOrNull { zipEntry ->
                val segments = zipEntry.name.split('/')
                val artIndex = segments.indexOf("Art")

                if (artIndex != -1) {
                    if (artIndex == 0) {
                        ""
                    } else {
                        segments.take(artIndex).joinToString("/") + "/"
                    }
                } else {
                    null
                }
            } ?: return targetPath

            for (entry in entries) {
                if (!entry.name.startsWith(assetsPrefix) || entry.name == assetsPrefix) {
                    continue
                }

                val relativePath = entry.name.removePrefix(assetsPrefix)
                val outputFile = File(targetPath, relativePath)

                if (!outputFile.canonicalPath.startsWith(canonicalTargetPath)) {
                    continue
                }

                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    zipFile.getInputStream(entry).use { inputStream ->
                        outputFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        }
        return targetPath
    }
}