package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.local.UserDataStoreApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.toMessage
import com.a.injector.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.io.File
import java.util.Locale

@Single
class SettingsRepositoryImpl(
    private val context: Context,
    private val userDataStoreApi: UserDataStoreApi
): SettingsRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            refreshCacheSize()
        }
    }

    override val language: Flow<Locale> = userDataStoreApi.language

    override fun setLanguage(locale: Locale): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            userDataStoreApi.setLanguage(
                locale = locale
            )
            emit(Either.Right(TextResource.StringResource(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    private val _cacheSize = MutableStateFlow(0L)
    override val cacheSize: Flow<Long> = _cacheSize.asStateFlow()

    override fun cleanCache(): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            val publicDownloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val cachePath = File(publicDownloadDir, context.getString(R.string.app_name))

            cachePath.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
            emit(Either.Right(TextResource.StringResource(R.string.action_cleared)))
            refreshCacheSize()
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }
    }

    private fun refreshCacheSize() {
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