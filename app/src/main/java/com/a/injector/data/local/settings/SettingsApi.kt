package com.a.injector.data.local.settings

import com.a.injector.domain.model.state.InjectMethod
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface SettingsApi {
    val injectMethod: Flow<InjectMethod>
    suspend fun setInjectMethod(injectMethod: InjectMethod)

    val language: Flow<Locale>
    suspend fun setLanguage(locale: Locale)
}