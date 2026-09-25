package com.a.injector.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.DatabaseRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AccountViewModel(
    private val accountRepository: AccountRepository,
    private val databaseRepository: DatabaseRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentUserInfo.filterNotNull().collect { userInfo ->
                _state.update { currentState ->
                    currentState.copy(userInfo = userInfo, isUserInfoLoading = false)
                }
            }
        }

        viewModelScope.launch {
            accountRepository.currentProfile.collect { profileModel ->
                _state.update { currentState ->
                    currentState.copy(profile = profileModel, isProfileLoading = false)
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
            is AccountAction.ShowUpsertProfileBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        profileToUpsert = action.profileModel,
                        isUpsertProfileBottomSheetVisible = true
                    )
                }
            }
            AccountAction.DismissUpsertProfileBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isUpsertProfileBottomSheetVisible = false)
                }
            }
            is AccountAction.UsernameTextField -> {
                _state.update { currentState ->
                    currentState.copy(
                        profileToUpsert = currentState.profileToUpsert.copy(username = action.username)
                    )
                }
            }
            AccountAction.UpsertProfileButton -> {
                upsertProfileButton()
            }
            AccountAction.RequestContributorButton -> {
                requestContributorButton()
            }
            AccountAction.CleanStorageBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isCleanStorageBottomSheetVisible = !currentState.isCleanStorageBottomSheetVisible)
                }
            }
            AccountAction.CleanStorageButton -> {
                cleanStorageButton()
            }
            AccountAction.ChangePasswordBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        isChangePasswordBottomSheetVisible = !currentState.isChangePasswordBottomSheetVisible
                    )
                }
            }
            is AccountAction.NewPasswordTextField -> {
                _state.update { currentState ->
                    currentState.copy(newPasswordTextField = action.password)
                }
            }
            AccountAction.ChangePasswordButton -> {
                changePasswordButton()
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

    private fun upsertProfileButton() {
        viewModelScope.launch {
            accountRepository.upsertProfile(
                profileModel = _state.value.profileToUpsert
            ).onStart {
                _state.update { it.copy(isUpsertProfileButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isUpsertProfileButtonLoading = false) }
            }.collect { either ->
                either.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
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
                either.onRight { message ->
                    _effect.send(ScreenEffect.ShowSnackBar(message))
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun cleanStorageButton() {
        viewModelScope.launch {
            databaseRepository.cleanStorage().onStart {
                _state.update { it.copy(isCleanStorageButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isCleanStorageButtonLoading = false) }
            }.collect { either ->
                either.onRight { message ->
                    _effect.send(ScreenEffect.ShowSnackBar(message))
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun changePasswordButton() {
        viewModelScope.launch {
            accountRepository.changePassword(
                password = _state.value.newPasswordTextField
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isChangePasswordButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(
                        isChangePasswordButtonLoading = false,
                        newPasswordTextField = ""
                    )
                }
            }.collect { either ->
                either.onRight { message ->
                    _effect.send(ScreenEffect.ShowSnackBar(message))
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
