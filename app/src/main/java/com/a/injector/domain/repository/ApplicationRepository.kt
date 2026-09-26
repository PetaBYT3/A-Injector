package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.VersionModel
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface ApplicationRepository {
    fun getVersion(): Flow<Either<TextResource, VersionModel>>

    val language: Flow<Locale>
    fun setLanguage(locale: Locale): Flow<Either<TextResource, Unit>>
    val cacheSize: Flow<Long>
    fun cleanCache(): Flow<Either<TextResource, TextResource>>
}