package com.a.injector.presentation.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.ProfileRepository
import com.a.injector.domain.repository.RoleRepository
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
class RoleViewModel(
    private val profileRepository: ProfileRepository,
    private val roleRepository: RoleRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(RoleState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
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

        viewModelScope.launch {
            roleRepository.getCurrent().collect { either ->
                either.onRight { roleModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            requestedRole = roleModel,
                            isRequestedRoleLoading = false
                        )
                    }
                }.onLeft { textResource ->
                    _state.update { currentState ->
                        currentState.copy(
                            isRequestedRoleError = textResource,
                            isRequestedRoleLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: RoleAction) {
        when (action) {
            RoleAction.RoleBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        isRoleBottomSheetVisible = !currentState.isRoleBottomSheetVisible
                    )
                }
            }
            is RoleAction.RoleButton -> {
                _state.update { currentState ->
                    currentState.copy(
                        requestedRole = currentState.requestedRole.copy(
                            role = action.role
                        )
                    )
                }
            }
            RoleAction.UpsertRoleButton -> {
                upsertRoleButton()
            }
        }
    }

    private fun upsertRoleButton() {
        viewModelScope.launch {
            roleRepository.upsert(
                roleModel = _state.value.requestedRole.copy(
                    id = _state.value.profile.id
                )
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isUpsertRoleButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isUpsertRoleButtonLoading = false)
                }
            }.collect { either ->
                either.onRight {
                    navigationRepository.popBackStack()
                }.onLeft { textResource ->
                    _effect.send(ScreenEffect.ShowSnackBar(textResource))
                }
            }
        }
    }
}