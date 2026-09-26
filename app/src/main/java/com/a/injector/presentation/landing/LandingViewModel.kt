package com.a.injector.presentation.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
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

@KoinViewModel
class LandingViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(LandingState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: LandingAction) {
        when (action) {
            LandingAction.ButtonSignGuest -> {
                buttonSignGuest()
            }
        }
    }

    private fun buttonSignGuest() {
        viewModelScope.launch {
            accountRepository.signGuest().onStart {
                _state.update { it.copy(isGuestButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isGuestButtonLoading = false) }
            }.collect { either ->
                either.onRight {
                    navigationRepository.replaceTo(MainNavigationRoute.BottomNavigation)
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}