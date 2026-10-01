package com.a.injector.presentation.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class PasswordViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(PasswordState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: PasswordAction) {
        when (action) {
            is PasswordAction.PasswordTextField -> {
                passwordTextField(password = action.password)
            }
            PasswordAction.ChangePasswordButton -> {
                changePasswordButton()
            }
        }
    }

    private fun passwordTextField(password: String) {
        val hasEightChar = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }
        val valid = hasEightChar && hasUppercase && hasNumber

        _state.update { currentState ->
            currentState.copy(
                passwordTextField = password,
                passwordHasEightChar = hasEightChar,
                passwordHasUppercase = hasUppercase,
                passwordHasNumber = hasNumber,
                passwordValid = valid
            )
        }
    }

    private fun changePasswordButton() {
        viewModelScope.launch {
            accountRepository.changePassword(
                password = _state.value.passwordTextField
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isChangePasswordButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isChangePasswordButtonLoading = false)
                }
            }.collect { either ->
                either.onRight { textResource ->
                    _effect.send(ScreenEffect.ShowSnackBar(textResource))
                    delay(2.seconds)
                    navigationRepository.popBackStack()
                }.onLeft { textResource ->
                    _effect.send(ScreenEffect.ShowSnackBar(textResource))
                }
            }
        }
    }
}