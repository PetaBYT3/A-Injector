package com.a.injector.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.ProfileRepository
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
class AccountViewModel(
    private val accountRepository: AccountRepository,
    private val profileRepository: ProfileRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentState.collect { authResult ->
                _state.update { currentState ->
                    currentState.copy(
                        authState = authResult,
                        isAuthStateLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            accountRepository.getCurrent().collect { either ->
                either.onRight { userInfo ->
                    _state.update { currentState ->
                        currentState.copy(
                            userInfo = userInfo,
                            isUserInfoLoading = false
                        )
                    }
                }.onLeft { textResource ->
                    _state.update { currentState ->
                        currentState.copy(
                            isUserInfoError = textResource,
                            isUserInfoLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            profileRepository.getCurrent().collect { either ->
                either.onRight { profileModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            profile = profileModel,
                            isProfileLoading = false
                        )
                    }
                }.onLeft { textResource ->
                    _state.update { currentState ->
                        currentState.copy(
                            isProfileError = textResource,
                            isProfileLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: AccountAction) {
        when (action) {
            AccountAction.SignOutBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        isSignOutBottomSheetVisible = !currentState.isSignOutBottomSheetVisible
                    )
                }
            }
            AccountAction.SignOutButton -> {
                signOutButton()
            }
        }
    }

    private fun signOutButton() {
        viewModelScope.launch {
            accountRepository.signOut().onStart {
                _state.update { it.copy(isSingOutButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isSingOutButtonLoading = false) }
            }.collect { either ->
                either.onRight {
                    navigationRepository.replaceTo(MainNavigationRoute.LandingScreen)
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}
