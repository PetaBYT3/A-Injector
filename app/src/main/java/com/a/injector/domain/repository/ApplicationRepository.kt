package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.VersionModel
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface ApplicationRepository {
    fun getVersion(): Flow<Either<Text, VersionModel>>

    val language: Flow<Locale>
    fun setLanguage(locale: Locale): Flow<Either<Text, Unit>>
    val cacheSize: Flow<Long>
    fun cleanCache(): Flow<Either<Text, Text>>
}