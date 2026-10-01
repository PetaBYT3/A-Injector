@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.remote.storage.StorageApi
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.state.Bucket
import com.a.injector.domain.repository.InjectRepository
import com.a.injector.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import java.io.File
import java.io.IOException
import java.util.zip.ZipFile

@Single
class InjectRepositoryImpl(
    private val context: Context,
    private val permissionRepository: PermissionRepository,
    private val storageApi: StorageApi
): InjectRepository {
    override fun execute(
        replaceModel: ReplaceModel
    ): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            val fileName = "${replaceModel.id}.zip"

            if (!permissionRepository.isManageExternalStorageGranted.first()) {
                emit(Either.Left(Text.Resource(R.string.exception_manage_storage_permission_denied)))
                return@flow
            }

            emit(Either.Right(Text.Resource(R.string.downloading)))
            val replaceFile = validateReplace(fileName, replaceModel.fileSize)

            emit(Either.Right(Text.Resource(R.string.extracting)))
            val extractedReplace = extractAssets(replaceFile)

            emit(Either.Right(Text.Resource(R.string.copying)))
            copyAssets(extractedReplace)

            emit(Either.Right(Text.Resource(R.string.success_script_install)))
        }.catchAndDispatch()
    }

    private suspend fun validateReplace(fileName: String, expectedSize: Long?): File {
        val publicDownloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetDownloadPath = File(publicDownloadPath, context.getString(R.string.app_name))

        if (!targetDownloadPath.exists()) {
            targetDownloadPath.mkdirs()
        }

        val downloadedFile = File(targetDownloadPath, fileName)
        if (downloadedFile.length() != expectedSize) {
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

    private fun copyAssets(targetPath: File) {
        val androidDir = File(Environment.getExternalStorageDirectory(), "Android")
        val actualData = File(androidDir, "data")
        val tempData = File(androidDir, "data1")

        val isRenamed = actualData.renameTo(tempData)
        if (!isRenamed && !tempData.exists()) {
            throw IOException("Gagal mengubah nama folder Android/data ke Android/data1.")
        }

        try {
            val targetAssetsTemp = File(tempData, "com.mobile.legends/files/dragon2017/assets")
            if (!targetAssetsTemp.exists()) {
                val created = targetAssetsTemp.mkdirs()
                if (!created && !targetAssetsTemp.exists()) {
                    throw IOException("Gagal membuat direktori target assets.")
                }
            }

            val isCopySuccess = targetPath.copyRecursively(
                target = targetAssetsTemp,
                overwrite = true,
                onError = { file, exception ->
                    throw IOException("Gagal menyalin file ${file.name}: ${exception.message}", exception)
                }
            )

            if (!isCopySuccess) {
                throw IOException("Proses penyalinan file tidak lengkap.")
            }
        } finally {
            synchronized(this) {
                if (actualData.exists()) {
                    var counter = 2
                    var osGeneratedData = File(androidDir, "data$counter")
                    while (osGeneratedData.exists()) {
                        counter++
                        osGeneratedData = File(androidDir, "data$counter")
                    }
                    actualData.renameTo(osGeneratedData)
                }
                tempData.renameTo(actualData)
            }
            cleanUpLeftoverDataFolders(androidDir)
        }
    }

    private fun cleanUpLeftoverDataFolders(androidDir: File) {
        val dataRegex = Regex("^data\\d+$")

        androidDir.listFiles()?.forEach { file ->
            if (file.isDirectory && file.name.matches(dataRegex)) {
                file.deleteRecursively()
            }
        }
    }
}