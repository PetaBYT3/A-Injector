package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface SettingsRepository {
    val language: Flow<Locale>
    fun setLanguage(locale: Locale): Flow<Either<TextResource, TextResource>>

    val cacheSize: Flow<Long>
    fun cleanCache(): Flow<Either<TextResource, TextResource>>
}