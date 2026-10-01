package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.local.settings.SettingsApi
import com.a.injector.data.mapper.VersionMapper
import com.a.injector.data.remote.version.VersionApi
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.VersionModel
import com.a.injector.domain.repository.ApplicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.io.File
import java.util.Locale

@Single
class ApplicationRepositoryImpl(
    private val context: Context,
    private val versionApi: VersionApi,
    private val settingsApi: SettingsApi
): ApplicationRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            updateCacheSize()
        }
    }

    override fun getVersion(): Flow<Either<Text, VersionModel>> {
        return versionApi.getSingle().map { versionDto ->
            if (versionDto != null) {
                Either.Right(VersionMapper.toModel(versionDto)) as Either<Text, VersionModel>
            } else {
                Either.Left(Text.Resource(R.string.exception_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override val language: Flow<Locale> = settingsApi.language

    override fun setLanguage(locale: Locale): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            settingsApi.setLanguage(
                locale = locale
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    private val _cacheSize = MutableStateFlow(0L)
    override val cacheSize: Flow<Long> = _cacheSize

    override fun cleanCache(): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            val publicDownloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val cachePath = File(publicDownloadDir, context.getString(R.string.app_name))
            cachePath.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
            emit(Either.Right(Text.Resource(R.string.success_clean_cache)))
            updateCacheSize()
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    private fun updateCacheSize() {
        val publicDownloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val cachePath = File(publicDownloadDir, context.getString(R.string.app_name))
        if (!cachePath.exists()) {
            _cacheSize.update { 0 }
            return
        }
        val totalSize = cachePath.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        _cacheSize.update { totalSize }
    }
}