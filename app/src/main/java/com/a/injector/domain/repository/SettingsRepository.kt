package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.VersionModel
import com.a.injector.domain.model.state.InjectMethod
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface SettingsRepository {
    fun getVersion(): Flow<Either<Text, VersionModel>>

    fun cleanStorage(): Flow<Either<Text, Text>>

    val injectMethod: Flow<InjectMethod>
    fun setInjectMethod(injectMethod: InjectMethod): Flow<Either<Text, Unit>>
    val language: Flow<Locale>
    fun setLanguage(locale: Locale): Flow<Either<Text, Unit>>
    val cacheSize: Flow<Long>
    fun cleanCache(): Flow<Either<Text, Text>>
}