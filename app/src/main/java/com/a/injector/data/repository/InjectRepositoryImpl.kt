@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.Bucket
import com.a.injector.data.local.UserDataStoreApi
import com.a.injector.data.remote.StorageApi
import com.a.injector.data.system.ShizukuCommandService
import com.a.injector.data.system.SuperuserCommandService
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.CommandServiceModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.state.CommandService
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

    override val commandService: Flow<CommandServiceModel> = combine(
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
            CommandService.StoragePermission -> {
                CommandServiceModel(
                    name = commandService,
                    isRunning = false
                )
            }
        }
    }

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

            validateReplace(fileName, replaceModel.fileSize)
            val extractedDir = extractAssets(File(fileName))
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
                        CommandService.StoragePermission -> {

                        }
                    }
                }
            }

            extractedDir.deleteRecursively()
            emit(Either.Right(context.getString(R.string.success_install_script)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun execute(replaceModel: ReplaceModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            val fileName = "${replaceModel.id}.zip"

            val replaceFile = validateReplace(fileName, replaceModel.fileSize)
            val extractedReplace = extractAssets(replaceFile)
            copyAssets(extractedReplace)

            emit(Either.Right(context.getString(R.string.success_install_script)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
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

            // 1. Cari prefix berdasarkan letak folder "Art"
            val assetsPrefix = entries.firstNotNullOfOrNull { zipEntry ->
                val segments = zipEntry.name.split('/')
                val artIndex = segments.indexOf("Art")

                if (artIndex != -1) {
                    if (artIndex == 0) {
                        // Jika "Art" ada di root ZIP (misal: "Art/UI/..."), prefix kosong ("")
                        ""
                    } else {
                        // Ambil semua folder SEBELUM "Art" sebagai root
                        // Misal: "base/assets/Art/..." -> prefix menjadi "base/assets/"
                        segments.take(artIndex).joinToString("/") + "/"
                    }
                } else {
                    null
                }
            } ?: return targetPath // Jika tidak ada folder "Art" sama sekali, kembalikan folder kosong

            for (entry in entries) {
                // Jika assetsPrefix "", startsWith("") akan selalu true
                if (!entry.name.startsWith(assetsPrefix) || entry.name == assetsPrefix) {
                    continue
                }

                // Hapus prefix parent-nya (jika ada)
                val relativePath = entry.name.removePrefix(assetsPrefix)
                val outputFile = File(targetPath, relativePath)

                // Validasi Zip Slip Protection
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

        val isBypassed = actualData.renameTo(tempData)
        if (!isBypassed) {
            throw Exception("Gagal me-rename Android/data. Pastikan izin MANAGE_EXTERNAL_STORAGE diberikan.")
        }

        try {
            // 2. Operasi Copy
            val targetAssetsTemp = File(tempData, "com.mobile.legends/files/dragon2017/assets")
            if (!targetAssetsTemp.exists()) {
                targetAssetsTemp.mkdirs()
            }

            val isCopySuccess = targetPath.copyRecursively(targetAssetsTemp, overwrite = true)
            if (!isCopySuccess) {
                throw Exception("Gagal menyalin file aset ke direktori target.")
            }

        } finally {
            // 3. Restorasi Super Cepat (Race Condition Handling)
            synchronized(this) {
                if (actualData.exists()) {
                    // Cari nama yang kosong (data2, data3, dst) agar tidak bentrok dengan sisa error masa lalu
                    var counter = 2
                    var osGeneratedData = File(androidDir, "data$counter")
                    while (osGeneratedData.exists()) {
                        counter++
                        osGeneratedData = File(androidDir, "data$counter")
                    }

                    // Pindahkan folder buatan OS secara INSTAN dari "data" menjadi "data2/3/.."
                    actualData.renameTo(osGeneratedData)
                }

                // Kembalikan data1 ke data dengan aman karena slot nama "data" sudah pasti kosong
                tempData.renameTo(actualData)
            }

            // 4. Proses Cleanup (Pembersihan Sisa Folder)
            // Dilakukan di luar blok synchronized karena ini operasi lambat dan tidak lagi berpacu dengan OS
            cleanUpLeftoverDataFolders(androidDir)
        }
    }

    private fun cleanUpLeftoverDataFolders(androidDir: File) {
        val dataRegex = Regex("^data\\d+$")

        androidDir.listFiles()?.forEach { file ->
            if (file.isDirectory && file.name.matches(dataRegex)) {
                try {
                    file.deleteRecursively()
                } catch (e: Exception) {
                    // Abaikan jika ada file yang nyangkut, akan dibersihkan di eksekusi berikutnya
                    e.printStackTrace()
                }
            }
        }
    }
}