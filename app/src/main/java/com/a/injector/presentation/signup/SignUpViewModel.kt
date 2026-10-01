package com.a.injector.presentation.signup

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
class SignUpViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.EmailTextField -> {
                _state.update { currentState ->
                    currentState.copy(emailTextField = action.email)
                }
            }
            is SignUpAction.PasswordTextField -> {
                passwordTextField(password = action.password)
            }
            SignUpAction.SignUpButton -> {
                signUpButton()
            }
        }
    }

    private fun passwordTextField(password: String) {
        val hasEightChar = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }

        _state.update { currentState ->
            currentState.copy(
                passwordTextField = password,
                passwordHasEightChar = hasEightChar,
                passwordHasUppercase = hasUppercase,
                passwordHasNumber = hasNumber,
            )
        }
    }

    private fun signUpButton() {
        viewModelScope.launch {
            accountRepository.signUp(
                email = _state.value.emailTextField,
                password = _state.value.passwordTextField
            ).onStart {
                _state.update { it.copy(isSignUpButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isSignUpButtonLoading = false) }
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