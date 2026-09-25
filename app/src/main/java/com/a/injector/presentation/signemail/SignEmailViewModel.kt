package com.a.injector.presentation.signemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
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

@KoinViewModel
class SignEmailViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(SignEmailState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: SignEmailAction) {
        when (action) {
            is SignEmailAction.EmailTextField -> {
                _state.update { currentState ->
                    currentState.copy(emailTextField = action.email)
                }
            }
            SignEmailAction.SendOtpButton -> {
                sendOtpButton()
            }
            is SignEmailAction.OtpTextField -> {
                _state.update { currentState ->
                    currentState.copy(otpTextField = action.otp)
                }
            }
            SignEmailAction.VerifyOtpButton -> {
                verifyOtpButton()
            }
        }
    }

    private fun sendOtpButton() {
        viewModelScope.launch {
            accountRepository.signOtp(
                email = _state.value.emailTextField
            ).onStart {
                _state.update { it.copy(isSendOtpButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isSendOtpButtonLoading = false) }
            }.collect { either ->
                either.onRight { message ->
                    _effect.send(ScreenEffect.ShowSnackBar(message))
                    _state.update { currentState ->
                        currentState.copy(isVerifyingOtp = true)
                    }
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun verifyOtpButton() {
        viewModelScope.launch {
            accountRepository.verifyOtp(
                email = _state.value.emailTextField,
                otp = _state.value.otpTextField
            ).onStart {
                _state.update { it.copy(isSendOtpButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isSendOtpButtonLoading = false) }
            }.collect { either ->
                either.onRight { message ->
                    navigationRepository.replaceTo(NavigationRoute.BottomNavigation)
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}