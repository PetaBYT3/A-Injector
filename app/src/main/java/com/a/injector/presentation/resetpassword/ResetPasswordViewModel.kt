package com.a.injector.presentation.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
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
class ResetPasswordViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: ResetPasswordAction) {
        when (action) {
            is ResetPasswordAction.EmailTextField -> {
                _state.update { currentState ->
                    currentState.copy(emailTextField = action.email)
                }
            }
            ResetPasswordAction.SendResetButton -> {
                sendResetButton()
            }
        }
    }

    private fun sendResetButton() {
        viewModelScope.launch {
            accountRepository.sendResetPassword(
                email = _state.value.emailTextField
            ).onStart {
                _state.update { it.copy(isSendResetButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isSendResetButtonLoading = false) }
            }.collect { either ->
                either.onRight {
                    navigationRepository.popBackStack()
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}