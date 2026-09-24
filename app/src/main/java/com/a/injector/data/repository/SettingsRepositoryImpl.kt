package com.a.injector.data.repository

import android.content.Context
import android.os.Environment
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.local.UserDataStoreApi
import com.a.injector.data.util.TextRes
import com.a.injector.data.util.toMessage
import com.a.injector.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single
import java.io.File
import java.util.Locale

@Single
class SettingsRepositoryImpl(
    private val context: Context,
    private val userDataStoreApi: UserDataStoreApi
): SettingsRepository {
    override val language: Flow<Locale> = userDataStoreApi.language

    override fun setLanguage(locale: Locale): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            userDataStoreApi.setLanguage(
                locale = locale
            )
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun cleanCache(): Flow<Either<TextRes, TextRes>> {
        return flow<Either<TextRes, TextRes>> {
            val publicDownloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val cachePath = File(publicDownloadDir, context.getString(R.string.app_name))

            cachePath.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
            emit(Either.Right(TextRes.StringResource(R.string.action_cleared)))
        }.catch { throwable ->
            emit(Either.Left(TextRes.DynamicString(throwable.toMessage(context))))
        }
    }
}