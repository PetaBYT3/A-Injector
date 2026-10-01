package com.a.injector.presentation.panelrole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.RoleRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PanelRoleViewModel(
    private val roleRepository: RoleRepository
): ViewModel() {
    private val _state = MutableStateFlow(PanelRoleState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            roleRepository.getList().collect { either ->
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

    fun onAction(action: PanelRoleAction) {
        when (action) {
            is PanelRoleAction.ShowGrantRequestBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        requestToAction = action.request,
                        isActionBottomSheetVisible = true
                    )
                }
            }
            PanelRoleAction.DismissGrantRequestBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isActionBottomSheetVisible = false)
                }
            }
            PanelRoleAction.DenyButton -> {
                denyButton()
            }
            PanelRoleAction.ConfirmButton -> {
                confirmButton()
            }
        }
    }

    private fun denyButton() {
        viewModelScope.launch {
            roleRepository.deny(
                roleModel = _state.value.requestToAction
            ).collect { either ->
                either.onLeft { textResource ->
                    _effect.send(ScreenEffect.ShowSnackBar(textResource))
                }
            }
        }
    }

    private fun confirmButton() {
        viewModelScope.launch {
            roleRepository.confirm(
                roleModel = _state.value.requestToAction
            ).collect { either ->
                either.onLeft { textResource ->
                    _effect.send(ScreenEffect.ShowSnackBar(textResource))
                }
            }
        }
    }
}