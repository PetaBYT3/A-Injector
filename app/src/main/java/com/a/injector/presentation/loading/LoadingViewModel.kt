package com.a.injector.presentation.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.navigation.NavigationRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class LoadingViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoadingState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.authState.filterNotNull().collect { currentAuth ->
                delay(1.5.seconds)
                when (currentAuth) {
                    AuthResult.Unauthenticated -> {
                        navigationRepository.replaceTo(NavigationRoute.LandingScreen)
                    }
                    AuthResult.Authenticated -> {
                        navigationRepository.replaceTo(NavigationRoute.BottomNavigation)
                    }
                }
            }
        }
    }
}