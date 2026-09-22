package com.a.injector.domain.repository

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface SettingsRepository {
    val language: Flow<Locale>
    fun setLanguage(locale: Locale): Flow<Either<String, String>>
}