package com.a.injector.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.AuthState
import com.a.injector.domain.model.RequestModel
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
class AccountViewModel(
    private val accountRepository: AccountRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentAuth.collect { authState ->
                if (authState is AuthState.Authorized) {
                    _state.update { currentState ->
                        currentState.copy(
                            userInfo = authState.userInfo,
                            profile = authState.profileModel,
                            isGuestAccount = false,
                            isProfileLoading = false
                        )
                    }
                } else {
                    _state.update { currentState ->
                        currentState.copy(
                            isGuestAccount = true,
                            isProfileLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            accountRepository.getRequestStatus().collect { requestState ->
                _state.update { currentState ->
                    currentState.copy(requestState = requestState)
                }
            }
        }
    }

    fun onAction(action: AccountAction) {
        when (action) {
            AccountAction.RequestContributorButton -> {
                requestContributorButton()
            }
            AccountAction.SignOutBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isSignOutBottomSheetVisible = !currentState.isSignOutBottomSheetVisible)
                }
            }
            AccountAction.SignOutButton -> {
                signOutButton()
            }
        }
    }

    private fun requestContributorButton() {
        viewModelScope.launch {
            accountRepository.upsertRequest(
                requestModel = RequestModel(
                    id = _state.value.profile.id,
                    role = Role.Contributor
                )
            ).collect { either ->
                either.onRight {
                    _effect.send(ScreenEffect.ShowSnackBar("Request Sent"))
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
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
                    navigationRepository.replaceTo(NavigationRoute.LandingScreen)
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}
