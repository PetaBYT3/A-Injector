package com.a.injector.presentation.managerole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.UserRepository
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
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(ManageRoleState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            userRepository.getRequests().collect { either ->
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
    }

    fun onAction(action: ManageRoleAction) {
        when (action) {
            is ManageRoleAction.ShowGrantRequestBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        requestToGrant = action.request,
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
        }
    }

    private fun grantRequestButton() {
        viewModelScope.launch {
            userRepository.grantRequest(
                requestModel = _state.value.requestToGrant
            ).collect { either ->
                either.onRight { message ->
                    _effect.send(ScreenEffect.ShowSnackBar(message))
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}