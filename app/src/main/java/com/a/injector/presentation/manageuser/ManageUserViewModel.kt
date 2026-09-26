package com.a.injector.presentation.manageuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.UserRepository
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
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class ManageUserViewModel(
    @InjectedParam private val profileId: String,
    private val userRepository: UserRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(ManageUserState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            userRepository.getProfile(
                profileId = profileId
            ).collect { either ->
                either.onRight { profileModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            profile = profileModel,
                            isProfileLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isProfileError = error,
                            isProfileLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: ManageUserAction) {
        when (action) {
            is ManageUserAction.NominalToAddTextField -> {
                _state.update { currentState ->
                    currentState.copy(nominalToAddTextField = action.nominalToAdd)
                }
            }
            ManageUserAction.SelectRoleBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isSelectRoleBottomSheetVisible = !currentState.isSelectRoleBottomSheetVisible)
                }
            }
            is ManageUserAction.SelectRoleButton -> {
                _state.update { currentState ->
                    currentState.copy(
                        profile = currentState.profile.copy(role = action.role)
                    )
                }
            }
            ManageUserAction.UpsertProfileButton -> {
                upsertProfileButton()
            }
        }
    }

    private fun upsertProfileButton() {
        viewModelScope.launch {
            val nominalToAdd = _state.value.nominalToAddTextField.ifBlank { "0" }
            val finalNominal = _state.value.profile.nominal + nominalToAdd.toLong()
            userRepository.upsertProfile(
                profileModel = _state.value.profile.copy(
                    nominal = finalNominal
                )
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isUpsertProfileButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isUpsertProfileButtonLoading = false)
                }
            }.collect { either ->
                either.onRight { message ->
                    navigationRepository.popBackStack()
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}