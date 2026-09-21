package com.a.injector.presentation.managerole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.DatabaseRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ManageRoleViewModel(
    private val accountRepository: AccountRepository,
    private val databaseRepository: DatabaseRepository
): ViewModel() {
    private val _state = MutableStateFlow(ManageRoleState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            accountRepository.getRequestDetails().collect { either ->
                either.onRight { requestDetailModels ->
                    _state.update { currentState ->
                        currentState.copy(
                            requestDetails = requestDetailModels,
                            isRequestDetailsLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isRequestDetailsError = error,
                            isRequestDetailsLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            accountRepository.getGrantedByRole(
                role = Role.Contributor
            ).collect { either ->
                either.onRight { profileModels ->
                    _state.update { currentState ->
                        currentState.copy(
                            contributorProfiles = profileModels,
                            isContributorProfilesLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isContributorProfilesError = error,
                            isContributorProfilesLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: ManageRoleAction) {
        when (action) {
            is ManageRoleAction.ShowGrantRequestBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        requestToGrant = action.requestModel,
                        isGrantRequestBottomSheetVisible = true
                    )
                }
            }
            ManageRoleAction.DismissGrantRequestBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isGrantRequestBottomSheetVisible = false)
                }
            }
            ManageRoleAction.GrantRequestButton -> {
                grantRequestButton()
            }
            is ManageRoleAction.ShowDetachProfileBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        profileToDetach = action.profileModel,
                        isDetachProfileBottomSheetVisible = true
                    )
                }
            }
            ManageRoleAction.DismissDetachProfileBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isDetachProfileBottomSheetVisible = false)
                }
            }
            ManageRoleAction.DetachProfileButton -> {
                detachProfileButton()
            }
        }
    }

    private fun grantRequestButton() {
        viewModelScope.launch {
            accountRepository.grantRequest(
                requestModel = RequestModel(
                    id = _state.value.requestToGrant.id,
                    role = _state.value.requestToGrant.role
                )
            ).collect { either ->
                either.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun detachProfileButton() {
        viewModelScope.launch {
            accountRepository.upsertProfile(
                profileModel = _state.value.profileToDetach.copy(
                    role = Role.User
                )
            ).collect { either ->
                either.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}