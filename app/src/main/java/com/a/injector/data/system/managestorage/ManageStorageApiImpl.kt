package com.a.injector.data.system.managestorage

import android.os.Environment
import com.a.injector.data.util.ManageStorageMethodFailed
import com.a.injector.data.util.ManageStoragePermissionDenied
import com.a.injector.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import java.io.File

@Single
class ManageStorageApiImpl(
    private val permissionRepository: PermissionRepository
): ManageStorageApi {
    override suspend fun copyToAndroidData(sourcePath: File, targetPath: String) {
        if (!permissionRepository.isManageExternalStorageGranted.first()) {
            throw ManageStoragePermissionDenied()
        }

        val androidDir = File(Environment.getExternalStorageDirectory(), "Android")
        val actual = File(androidDir, "data")
        val temp = File(androidDir, "data1")

        var isRenamedSuccessfully = false

        try {
            isRenamedSuccessfully = actual.renameTo(temp)

            if (!isRenamedSuccessfully) {
                throw ManageStorageMethodFailed()
            }

            val targetTemp = File(temp, targetPath)
            if (!targetTemp.exists() && !targetTemp.mkdirs()) {
                throw ManageStorageMethodFailed()
            }

            val copySuccess = sourcePath.copyRecursively(
                target = targetTemp,
                overwrite = true
            )

            if (!copySuccess) {
                throw ManageStorageMethodFailed()
            }
        } catch (e: Exception) {
            throw ManageStorageMethodFailed()
        } finally {
            if (isRenamedSuccessfully) {
                synchronized(this) {
                    if (actual.exists()) {
                        var counter = 2
                        var osGeneratedData = File(androidDir, "data$counter")
                        while (osGeneratedData.exists()) {
                            counter++
                            osGeneratedData = File(androidDir, "data$counter")
                        }
                        actual.renameTo(osGeneratedData)
                    }
                    temp.renameTo(actual)
                }

                val unUsedDataRegex = Regex("^data\\d+$")
                androidDir.listFiles()?.forEach { file ->
                    if (file.isDirectory && file.name.matches(unUsedDataRegex)) {
                        file.deleteRecursively()
                    }
                }
            }
        }
    }
}