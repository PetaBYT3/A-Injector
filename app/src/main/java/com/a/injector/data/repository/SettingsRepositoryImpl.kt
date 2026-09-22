package com.a.injector.data.repository

import android.content.Context
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.local.UserDataStoreApi
import com.a.injector.data.util.toMessage
import com.a.injector.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single
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
}