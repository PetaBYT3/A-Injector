package com.a.injector.presentation.panelsupporting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.dto.Bucket
import com.a.injector.domain.model.SupportingModel
import com.a.injector.domain.repository.OptimizeDatabaseRepository
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

@KoinViewModel
class PanelSupportingViewModel(
    private val userRepository: UserRepository,
    private val optimizeDatabaseRepository: OptimizeDatabaseRepository
): ViewModel() {
    private val _state = MutableStateFlow(PanelSupportingState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            userRepository.getSupportingList().collect { either ->
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

    fun onAction(action: PanelSupportingAction) {
        when (action) {
            is PanelSupportingAction.ShowSupportingBottomSheet -> {
                showSupportingBottomSheet(supportingModel = action.supportingModel)
            }
            PanelSupportingAction.DismissSupportingBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isSupportingBottomSheetVisible = false)
                }
            }
            PanelSupportingAction.DenySupportingButton -> {
                denySupportingButton()
            }
            PanelSupportingAction.ConfirmSupportingButton -> {
                confirmSupportingButton()
            }
        }
    }

    private fun showSupportingBottomSheet(supportingModel: SupportingModel) {
        _state.update { currentState ->
            currentState.copy(
                supportingToAction = supportingModel,
                isSupportingBottomSheetVisible = true
            )
        }
        viewModelScope.launch {
            optimizeDatabaseRepository.getFileUrl(
                bucket = Bucket.IMAGE,
                fileName = supportingModel.imageUrl
            ).collect { either ->
                either.onRight { url ->
                    _state.update { currentState ->
                        currentState.copy(proofUrlToAction = url)
                    }
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun denySupportingButton() {
        viewModelScope.launch {
            userRepository.denySupporting(
                supportingModel = _state.value.supportingToAction
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = false)
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

    private fun confirmSupportingButton() {
        viewModelScope.launch {
            userRepository.confirmSupporting(
                supportingModel = _state.value.supportingToAction
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isActionSupportingButtonLoading = false)
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
}