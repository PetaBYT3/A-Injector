package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.local.settings.SettingsApi
import com.a.injector.data.mapper.VersionMapper
import com.a.injector.data.remote.replace.ReplaceApi
import com.a.injector.data.remote.storage.StorageApi
import com.a.injector.data.remote.support.SupportApi
import com.a.injector.data.remote.version.VersionApi
import com.a.injector.data.system.directory.DirectoryApi
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.VersionModel
import com.a.injector.domain.model.state.Bucket
import com.a.injector.domain.model.state.Directory
import com.a.injector.domain.model.state.InjectMethod
import com.a.injector.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.io.File
import java.util.Locale

@Single
class SettingsRepositoryImpl(
    private val context: Context,
    private val directoryApi: DirectoryApi,
    private val versionApi: VersionApi,
    private val replaceApi: ReplaceApi,
    private val supportApi: SupportApi,
    private val storageApi: StorageApi,
    private val settingsApi: SettingsApi
): SettingsRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            updateCacheSize()
        }
    }

    override fun getVersion(): Flow<Either<Text, VersionModel>> {
        return flow<Either<Text, VersionModel>> {
            val result = versionApi.getSingle().map { versionDto ->
                if (versionDto != null) {
                    Either.Right(VersionMapper.toModel(versionDto))
                } else {
                    Either.Left(Text.Resource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun cleanStorage(): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            val validScriptFiles = replaceApi.getList().first().map { replaceDto ->
                "${replaceDto.id}.zip"
            }
            val validImageFiles = supportApi.getList().first().map { supportDto ->
                "${supportDto.id}.webp"
            }

            val orphanScripts = storageApi.getFileNames(Bucket.SCRIPT).filter { fileName ->
                fileName !in validScriptFiles
            }
            val orphanImages = storageApi.getFileNames(Bucket.IMAGE).filter { fileName ->
                fileName !in validImageFiles
            }

            if (orphanScripts.isNotEmpty()) {
                storageApi.delete(
                    fromBucket = Bucket.SCRIPT,
                    files = orphanScripts
                )
            }

            if (orphanImages.isNotEmpty()) {
                storageApi.delete(
                    fromBucket = Bucket.IMAGE,
                    files = orphanImages
                )
            }
            emit(Either.Right(Text.Resource(R.string.success_clean_cloud_storage)))
        }.catchAndDispatch()
    }

    override val injectMethod: Flow<InjectMethod> = settingsApi.injectMethod

    override fun setInjectMethod(injectMethod: InjectMethod): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            settingsApi.setInjectMethod(
                injectMethod = injectMethod
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override val language: Flow<Locale> = settingsApi.language

    override fun setLanguage(locale: Locale): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            settingsApi.setLanguage(
                locale = locale
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    private val _cacheSize = MutableStateFlow(0L)
    override val cacheSize: Flow<Long> = _cacheSize

    override fun cleanCache(): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            val downloadedDir = directoryApi.getDirectory(Directory.Downloaded)
            val extractedDir = directoryApi.getDirectory(Directory.Extracted)

            downloadedDir.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
            extractedDir.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
            emit(Either.Right(Text.Resource(R.string.success_clean_cache)))
            updateCacheSize()
        }.catchAndDispatch()
    }

    private fun updateCacheSize() {
        val downloadedDir = directoryApi.getDirectory(Directory.Downloaded)
        val extractedDir = directoryApi.getDirectory(Directory.Extracted)

        val downloadedSize = if (downloadedDir.exists()) {
            downloadedDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        } else 0L

        val extractedSize = if (extractedDir.exists()) {
            extractedDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        } else 0L

        _cacheSize.update { downloadedSize + extractedSize }
    }
}