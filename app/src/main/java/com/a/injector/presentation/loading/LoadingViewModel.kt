package com.a.injector.presentation.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class LoadingViewModel(
    private val signMethod: SignMethod,
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoadingState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            when (signMethod) {
                SignMethod.EmailPassword -> {
                    accountRepository.getAuthState().collect { currentAuth ->
                        delay(1.5.seconds)
                        when (currentAuth) {
                            AuthResult.Unauthenticated -> {
                                navigationRepository.replaceTo(MainNavigationRoute.LandingScreen)
                            }
                            AuthResult.Authenticated -> {
                                navigationRepository.replaceTo(MainNavigationRoute.BottomNavigation)
                            }
                        }
                    }
                }
                SignMethod.EmailLink -> {
                    val authResult = withTimeoutOrNull(10.seconds) {
                        accountRepository.getAuthState().first { it == AuthResult.Authenticated }
                    }

                    if (authResult != null) {
                        navigationRepository.replaceTo(MainNavigationRoute.BottomNavigation)
                    } else {
                        navigationRepository.replaceTo(MainNavigationRoute.LandingScreen)
                    }
                }
            }
        }
    }
}