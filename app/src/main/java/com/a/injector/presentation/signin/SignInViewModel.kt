package com.a.injector.presentation.signin

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
class SignInViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: SignInAction) {
        when (action) {
            is SignInAction.EmailTextField -> {
                _state.update { currentState ->
                    currentState.copy(emailTextField = action.email)
                }
            }
            is SignInAction.PasswordTextField -> {
                _state.update { currentState ->
                    currentState.copy(passwordTextField = action.password)
                }
            }
            SignInAction.SignInButton -> {
                signInButton()
            }
        }
    }

    private fun signInButton() {
        viewModelScope.launch {
            accountRepository.signIn(
                email = _state.value.emailTextField,
                password = _state.value.passwordTextField
            ).onStart {
                _state.update { it.copy(isSingInButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isSingInButtonLoading = false) }
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