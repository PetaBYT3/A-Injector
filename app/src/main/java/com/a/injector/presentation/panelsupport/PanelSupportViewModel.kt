package com.a.injector.presentation.panelsupport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.SupportRepository
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
class PanelSupportViewModel(
    private val supportRepository: SupportRepository
): ViewModel() {
    private val _state = MutableStateFlow(PanelSupportState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            supportRepository.getList().collect { either ->
                either.onRight { supportingModels ->
                    _state.update { currentState ->
                        currentState.copy(
                            supportingList = supportingModels,
                            isSupportingListLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isSupportingListError = error,
                            isSupportingListLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: PanelSupportAction) {
        when (action) {
            is PanelSupportAction.ShowSupportBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        supportingToAction = action.supportModel,
                        isSupportingBottomSheetVisible = true
                    )
                }
            }
            PanelSupportAction.DismissSupportBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isSupportingBottomSheetVisible = false)
                }
            }
            PanelSupportAction.DenySupportButton -> {
                denySupportingButton()
            }
            PanelSupportAction.ConfirmSupportButton -> {
                confirmSupportingButton()
            }
        }
    }

    private fun denySupportingButton() {
        viewModelScope.launch {
            supportRepository.confirm(
                supportModel = _state.value.supportingToAction
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = false)
                }
            }.collect { either ->
                either.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun confirmSupportingButton() {
        viewModelScope.launch {
            supportRepository.deny(
                supportModel = _state.value.supportingToAction
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = false)
                }
            }.collect { either ->
                either.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}