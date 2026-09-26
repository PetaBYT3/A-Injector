package com.a.injector.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.OptimizeDatabaseRepository
import com.a.injector.domain.repository.UserRepository
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AccountViewModel(
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository,
    private val optimizeDatabaseRepository: OptimizeDatabaseRepository,
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
            _state.map { currentState ->
                currentState.profile.id
            }.distinctUntilChanged().filter { profileId ->
                profileId.isNotBlank()
            }.collect { profileId ->
                userRepository.getRequest(profileId).collect { either ->
                    either.onRight { requestModel ->
                        _state.update { currentState ->
                            currentState.copy(request = requestModel, isRequestLoading = false)
                        }
                    }.onLeft { error ->
                        _state.update { currentState ->
                            currentState.copy(isRequestLoading = false)
                        }
                    }
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
            AccountAction.RequestRoleBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isRequestRoleBottomSheetVisible = !currentState.isRequestRoleBottomSheetVisible)
                }
            }
            is AccountAction.RequestRoleButton -> {
                requestRoleButton(role = action.role)
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
            userRepository.upsertProfile(
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

    private fun requestRoleButton(role: Role) {
        viewModelScope.launch {
            userRepository.upsertRequest(
                requestModel = RequestModel(
                    id = _state.value.profile.id,
                    role = role,
                    profile = ProfileModel.EMPTY
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
            optimizeDatabaseRepository.cleanStorage().onStart {
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
                    navigationRepository.replaceTo(MainNavigationRoute.LandingScreen)
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}
