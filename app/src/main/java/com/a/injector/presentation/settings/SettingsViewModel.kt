package com.a.injector.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.SettingsRepository
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.util.Locale

@KoinViewModel
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            settingsRepository.language.collect { locale ->
                _state.update { currentState ->
                    currentState.copy(currentLanguage = locale)
                }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.LanguageBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isLanguageBottomSheetVisible = !currentState.isLanguageBottomSheetVisible)
                }
            }
            is SettingsAction.SetLanguageButton -> {
                setLanguageButton(locale = action.locale)
            }

            SettingsAction.CleanCacheBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isClearCacheBottomSheetVisible = !currentState.isClearCacheBottomSheetVisible)
                }
            }
            SettingsAction.CleanCacheButton -> {
                cleanCacheButton()
            }
        }
    }

    private fun setLanguageButton(locale: Locale) {
        viewModelScope.launch {
            settingsRepository.setLanguage(
                locale = locale
            ).collect { either ->
                either.onRight {
                    navigationRepository.replaceTo(NavigationRoute.LoadingScreen)
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun cleanCacheButton() {
        viewModelScope.launch {
            settingsRepository.cleanCache().onStart {
                _state.update { it.copy(isClearCacheButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isClearCacheButtonLoading = false) }
            }.collect { either ->
                either.onRight { textRes ->

                }.onLeft { textRes ->
                    _effect.send(ScreenEffect.ShowSnackBar(textRes.asString(context = )))
                }
            }
        }
    }
}