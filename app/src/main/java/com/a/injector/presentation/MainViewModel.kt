package com.a.injector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.model.AuthState
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.navigation.NavigationRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class MainViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _isSplashScreenVisible = MutableStateFlow(true)
    val isSplashScreenVisible = _isSplashScreenVisible.asStateFlow()

    private var isInitialized = false

    init {
        viewModelScope.launch {
            accountRepository.currentAuth.onStart {
                navigationRepository.navigateTo(NavigationRoute.LoadingScreen)
            }.onCompletion {
                navigationRepository.popBackStack()
            }.collect { authState ->
                if (!isInitialized) {
                    when (authState) {
                        AuthState.Unauthorized -> {
                            navigationRepository.replaceTo(NavigationRoute.LandingScreen)
                        }
                        is AuthState.Authorized -> {
                            navigationRepository.replaceTo(NavigationRoute.BottomNavigation)
                        }
                        AuthState.Guest -> {
                            navigationRepository.replaceTo(NavigationRoute.BottomNavigation)
                        }
                    }
                    _isSplashScreenVisible.update { false }
                    delay(1.5.seconds)
                    isInitialized = true
                } else {
                    when {
                        authState is AuthState.Unauthorized -> {
                            navigationRepository.replaceTo(NavigationRoute.BottomNavigation)
                        }
                    }
                }
            }
        }
    }
}